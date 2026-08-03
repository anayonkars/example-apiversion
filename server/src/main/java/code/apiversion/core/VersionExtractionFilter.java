package code.apiversion.core;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.UriInfo;

/**
 * A JAX-RS filter that extracts the API version from the request path (e.g.,
 * /v1/resource)
 * and rewrites the path to be version-agnostic (e.g., /resource).
 * The extracted version is stored in {@link RequestVersionContext}.
 */
@Provider
@PreMatching
@Priority(500)
public class VersionExtractionFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(VersionExtractionFilter.class.getName());
    private static final Pattern V_PATTERN = Pattern.compile("/v(\\d+)/");

    /** Lowest version a client may ask for. */
    static final int MIN_VERSION = 1;

    /**
     * Longest digit run accepted as a version. Nine digits cannot overflow an int,
     * so {@link Integer#parseInt} below is total for any token that gets this far.
     */
    private static final int MAX_VERSION_DIGITS = 9;

    /**
     * Inspects the request URI for a version prefix (e.g., /v1/).
     * If found, extracts the version, stores it, and rewrites the request URI
     * to remove the version prefix, allowing standard routing to proceed.
     * <p>
     * A syntactically well-formed but out-of-range version (v0, or a number too
     * large to be a plausible version) is rejected with 404 rather than passed
     * downstream: the version is attacker-controlled, and an unbounded value used
     * to be able to overflow the parse or drive an expensive fallback scan.
     *
     * @param requestContext The container request context.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Never inherit a version from a previous request on this pooled thread; the
        // response filter below normally clears it, but it does not run on every path.
        RequestVersionContext.clearVersion();

        UriInfo uriInfo = requestContext.getUriInfo();
        String path = uriInfo.getPath();
        LOG.fine(() -> "VersionExtractionFilter path: " + path);
        Matcher matcher = V_PATTERN.matcher(path);
        if (!matcher.find()) {
            return;
        }

        Integer apiVersion = parseVersion(matcher.group(1));
        if (apiVersion == null) {
            LOG.fine(() -> "Rejecting unsupported API version token: v" + matcher.group(1));
            requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
            return;
        }

        LOG.fine(() -> "Extracted API version: " + apiVersion);
        RequestVersionContext.setVersion(apiVersion);
        String newPath = matcher.replaceFirst("/");

        // Ensure the new path is treated as relative to base URI
        if (newPath.startsWith("/")) {
            newPath = newPath.substring(1);
        }
        if (newPath.isEmpty()) {
            requestContext.setRequestUri(uriInfo.getBaseUri());
        } else {
            requestContext.setRequestUri(uriInfo.getBaseUri().resolve(newPath));
        }
    }

    /**
     * Parses a version token from the path.
     *
     * @param token A run of digits captured by {@link #V_PATTERN}.
     * @return The version, or null if it is too long or below {@link #MIN_VERSION}.
     */
    private static Integer parseVersion(String token) {
        if (token.length() > MAX_VERSION_DIGITS) {
            return null;
        }
        int version = Integer.parseInt(token);
        return version < MIN_VERSION ? null : version;
    }

    /**
     * Clears the ThreadLocal version context after the response is complete.
     * This is essential in servlet containers where threads are pooled and reused:
     * without this cleanup, a thread that handled a versioned request (e.g., /v2/resource)
     * could carry that version into the next unversioned request it processes,
     * causing incorrect routing. The request filter also clears the context on entry,
     * so a skipped response filter cannot strand a stale version either.
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        RequestVersionContext.clearVersion();
    }
}
