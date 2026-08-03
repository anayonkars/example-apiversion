package code.apiversion.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @AfterEach
    public void tearDown() {
        RequestVersionContext.clearVersion();
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

        // verify setRequestUri is NOT called (single-arg overload used by the filter)
        verify(requestContext, org.mockito.Mockito.never()).setRequestUri(any(URI.class));
    }

    @Test
    public void testFilterHandlesVersionInMiddleOfPath() throws IOException {
        String originalPath = "/api/v2/resource";
        URI baseUri = URI.create("http://localhost:8800/");

        when(uriInfo.getPath()).thenReturn(originalPath);
        when(uriInfo.getBaseUri()).thenReturn(baseUri);

        filter.filter(requestContext);

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestContext).setRequestUri(uriCaptor.capture());

        // /api/v2/resource -> /api/resource
        assertEquals("http://localhost:8800/api/resource", uriCaptor.getValue().toString());
        assertEquals(2, RequestVersionContext.getVersion());
    }

    @Test
    public void testFilterHandlesEmptyPathAfterRewrite() throws IOException {
        String originalPath = "/v1/";
        URI baseUri = URI.create("http://localhost:8800/");

        when(uriInfo.getPath()).thenReturn(originalPath);
        when(uriInfo.getBaseUri()).thenReturn(baseUri);

        filter.filter(requestContext);

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestContext).setRequestUri(uriCaptor.capture());

        assertEquals("http://localhost:8800/", uriCaptor.getValue().toString());
        assertEquals(1, RequestVersionContext.getVersion());
    }

    @Test
    public void testFilterVersionAtStartOfPath() throws IOException {
        String originalPath = "/v2/resource";
        URI baseUri = URI.create("http://localhost:8800/");

        when(uriInfo.getPath()).thenReturn(originalPath);
        when(uriInfo.getBaseUri()).thenReturn(baseUri);

        filter.filter(requestContext);

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestContext).setRequestUri(uriCaptor.capture());

        assertEquals("http://localhost:8800/resource", uriCaptor.getValue().toString());
        assertEquals(2, RequestVersionContext.getVersion());
    }

    @Test
    public void testFilterRejectsZeroVersionWithNotFound() throws IOException {
        when(uriInfo.getPath()).thenReturn("/example/v0/greeting");

        filter.filter(requestContext);

        assertAbortedWithNotFound();
        assertNull(RequestVersionContext.getVersion());
        verify(requestContext, org.mockito.Mockito.never()).setRequestUri(any(URI.class));
    }

    @Test
    public void testFilterRejectsOversizedVersionWithNotFound() throws IOException {
        // Would have overflowed Integer.parseInt and escaped as an HTTP 500.
        when(uriInfo.getPath()).thenReturn("/example/v99999999999999/greeting");

        filter.filter(requestContext);

        assertAbortedWithNotFound();
        assertNull(RequestVersionContext.getVersion());
    }

    @Test
    public void testFilterAcceptsLargestNonOverflowingVersion() throws IOException {
        when(uriInfo.getPath()).thenReturn("/example/v999999999/greeting");
        when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:8800/"));

        filter.filter(requestContext);

        assertEquals(999999999, RequestVersionContext.getVersion());
    }

    @Test
    public void testFilterClearsStaleVersionFromPooledThread() throws IOException {
        // Simulate a thread whose previous request left a version behind.
        RequestVersionContext.setVersion(2);
        when(uriInfo.getPath()).thenReturn("/example/greeting");

        filter.filter(requestContext);

        assertNull(RequestVersionContext.getVersion());
    }

    private void assertAbortedWithNotFound() {
        ArgumentCaptor<Response> responseCaptor = ArgumentCaptor.forClass(Response.class);
        verify(requestContext).abortWith(responseCaptor.capture());
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), responseCaptor.getValue().getStatus());
    }

    @Test
    public void testResponseFilterClearsVersion() throws IOException {
        RequestVersionContext.setVersion(5);

        ContainerResponseContext responseContext = mock(ContainerResponseContext.class);
        filter.filter(requestContext, responseContext);

        assertNull(RequestVersionContext.getVersion());
    }
}
