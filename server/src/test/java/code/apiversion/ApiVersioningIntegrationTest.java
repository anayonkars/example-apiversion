package code.apiversion;

import code.apiversion.core.RequestVersionContext;
import code.apiversion.core.VersionExtractionFilter;
import code.apiversion.core.VersionRoutingHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ApiVersioningIntegrationTest {

    private VersionExtractionFilter filter;
    private VersionRoutingHandler<TestResource> router;
    private ContainerRequestContext requestContext;
    private UriInfo uriInfo;
    private TestResource v1Resource;
    private TestResource v2Resource;
    private Map<Integer, TestResource> implementations;

    public interface TestResource {
        String get();
    }

    @BeforeEach
    public void setUp() {
        // Setup Filter
        filter = new VersionExtractionFilter();
        requestContext = mock(ContainerRequestContext.class);
        uriInfo = mock(UriInfo.class);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:8800/"));

        // Setup Router
        v1Resource = mock(TestResource.class);
        v2Resource = mock(TestResource.class);
        implementations = new HashMap<>();
        implementations.put(1, v1Resource);
        implementations.put(2, v2Resource);
        router = new VersionRoutingHandler<>(implementations);

        when(v1Resource.get()).thenReturn("v1");
        when(v2Resource.get()).thenReturn("v2");
    }

    @AfterEach
    public void tearDown() {
        RequestVersionContext.clearVersion();
    }

    @Test
    public void testV2RequestIsRoutedToV2Implementation() throws Throwable {
        // 1. Filter Step: Request comes in as /v2/resource
        when(uriInfo.getPath()).thenReturn("/v2/resource");

        filter.filter(requestContext);

        // Verify Filter extracted version 2
        assertEquals(2, RequestVersionContext.getVersion());

        // Verify Rewrite happened
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestContext).setRequestUri(uriCaptor.capture());
        assertEquals("http://localhost:8800/resource", uriCaptor.getValue().toString());

        // 2. Routing Step: Router invoked
        Method method = TestResource.class.getMethod("get");
        router.invoke(null, method, null);

        // Verify V2 was called
        verify(v2Resource).get();
        verify(v1Resource, never()).get();
    }

    @Test
    public void testV3RequestFallsBackToV2Implementation() throws Throwable {
        // 1. Filter Step: Request comes in as /v3/resource
        when(uriInfo.getPath()).thenReturn("/v3/resource");

        filter.filter(requestContext);

        // Verify Filter extracted version 3
        assertEquals(3, RequestVersionContext.getVersion());

        // 2. Routing Step
        Method method = TestResource.class.getMethod("get");
        router.invoke(null, method, null);

        // Verify V2 was called (fallback)
        verify(v2Resource).get();
        verify(v1Resource, never()).get();
    }
}
