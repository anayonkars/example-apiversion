package code.apiversion.core;

import code.apiversion.service.ExampleService;
import code.apiversion.service.ExampleServiceImplV1;
import code.apiversion.service.ExampleServiceImplV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VersionedProxyProviderTest {

    @AfterEach
    public void tearDown() {
        RequestVersionContext.clearVersion();
    }

    @Test
    public void testGetReturnsNonNullProxy() {
        VersionedProxyProvider<ExampleService> provider = providerWith(Map.of(1, new ExampleServiceImplV1()));
        assertNotNull(provider.get());
    }

    @Test
    public void testProxyImplementsInterface() {
        VersionedProxyProvider<ExampleService> provider = providerWith(Map.of(1, new ExampleServiceImplV1()));
        assertInstanceOf(ExampleService.class, provider.get());
    }

    @Test
    public void testProxyRoutesV1Calls() {
        VersionedProxyProvider<ExampleService> provider = providerWith(Map.of(
                1, new ExampleServiceImplV1(),
                2, new ExampleServiceImplV2()));
        RequestVersionContext.setVersion(1);
        assertTrue(provider.get().getGreeting().contains("v1"));
    }

    @Test
    public void testProxyRoutesV2Calls() {
        VersionedProxyProvider<ExampleService> provider = providerWith(Map.of(
                1, new ExampleServiceImplV1(),
                2, new ExampleServiceImplV2()));
        RequestVersionContext.setVersion(2);
        assertTrue(provider.get().getGreeting().contains("v2"));
    }

    @Test
    public void testEachGetCallReturnsNewProxy() {
        VersionedProxyProvider<ExampleService> provider = providerWith(Map.of(1, new ExampleServiceImplV1()));
        assertNotSame(provider.get(), provider.get());
    }

    private VersionedProxyProvider<ExampleService> providerWith(Map<Integer, ExampleService> impls) {
        return new VersionedProxyProvider<>(ExampleService.class, impls);
    }
}
