package code.apiversion.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VersionExtractionFilterTest {

    private VersionExtractionFilter filter;
    private ContainerRequestContext requestContext;
    private UriInfo uriInfo;

    @BeforeEach
    public void setUp() {
        filter = new VersionExtractionFilter();
        requestContext = mock(ContainerRequestContext.class);
        uriInfo = mock(UriInfo.class);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
    }

    @Test
    public void testFilterExtractsVersionAndRewritesPath() throws IOException {
        String originalPath = "/example/v3/greeting";
        URI baseUri = URI.create("http://localhost:8800/");

        when(uriInfo.getPath()).thenReturn(originalPath);
        when(uriInfo.getBaseUri()).thenReturn(baseUri);

        filter.filter(requestContext);

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestContext).setRequestUri(uriCaptor.capture());

        URI capturedUri = uriCaptor.getValue();
        // The filter now resolves path against base URI.
        // Base: http://localhost:8800/
        // Path: /example/v3/greeting -> rewritten relative to example/greeting
        // Result: http://localhost:8800/example/greeting
        assertEquals("http://localhost:8800/example/greeting", capturedUri.toString());
        assertEquals(3, RequestVersionContext.getVersion());
    }

    @Test
    public void testFilterIgnoresPathWithoutVersion() throws IOException {
        String originalPath = "/example/greeting";
        when(uriInfo.getPath()).thenReturn(originalPath);

        filter.filter(requestContext);

        // verify setRequestUri is NOT called
        verify(requestContext, org.mockito.Mockito.never()).setRequestUri(any(), any());
    }
}
