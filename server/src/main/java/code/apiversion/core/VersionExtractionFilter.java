package code.apiversion.core;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
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

    /**
     * Inspects the request URI for a version prefix (e.g., /v1/).
     * If found, extracts the version, stores it, and rewrites the request URI
     * to remove the version prefix, allowing standard routing to proceed.
     *
     * @param requestContext The container request context.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        String path = uriInfo.getPath();
        LOG.fine(() -> "VersionExtractionFilter path: " + path);
        Matcher matcher = V_PATTERN.matcher(path);
        if (matcher.find()) {
            int apiVersion = Integer.parseInt(matcher.group(1));
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
    }

    /**
     * Clears the ThreadLocal version context after the response is complete.
     * This is essential in servlet containers where threads are pooled and reused:
     * without this cleanup, a thread that handled a versioned request (e.g., /v2/resource)
     * could carry that version into the next unversioned request it processes,
     * causing incorrect routing. The request filter only sets the version when a
     * version prefix is present — it does not clear stale values from prior requests.
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        RequestVersionContext.clearVersion();
    }
}
