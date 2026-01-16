package code.apiversion.core;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.UriInfo;

@Provider
@PreMatching
@Priority(500)
public class VersionExtractionFilter implements ContainerRequestFilter {
    private static final Pattern V_PATTERN = Pattern.compile("/v(\\d+)/");

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("Inside VersionExtractionFilter");
        UriInfo uriInfo = requestContext.getUriInfo();
        String path = uriInfo.getPath();
        System.out.println("Path is: " + path);
        Matcher matcher = V_PATTERN.matcher(path);
        if (matcher.find()) {
            Integer apiVersion = Integer.parseInt(matcher.group(1));
            System.out.println("Version received is: " + apiVersion);
            RequestVersionContext.setVersion(apiVersion);
            String newPath = matcher.replaceFirst("");
            if (newPath.isEmpty()) {
                newPath = "/";
            }
            requestContext.setRequestUri(uriInfo.getBaseUri(), URI.create(newPath));
        }
    }
}
