package code.apiversion.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VersionRoutingHandlerTest {

    private Map<Integer, TestInterface> implementations;
    private VersionRoutingHandler<TestInterface> handler;
    private TestInterface v1Mock;
    private TestInterface v2Mock;
    private Method testMethod;

    public interface TestInterface {
        String test();
    }

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        v1Mock = mock(TestInterface.class);
        v2Mock = mock(TestInterface.class);

        implementations = new HashMap<>();
        implementations.put(1, v1Mock);
        implementations.put(2, v2Mock);

        handler = new VersionRoutingHandler<>(implementations);
        testMethod = TestInterface.class.getMethod("test");
    }

    @AfterEach
    public void tearDown() {
        RequestVersionContext.clearVersion();
    }

    @Test
    public void testInvokeUsesExactVersion() throws Throwable {
        RequestVersionContext.setVersion(1);
        handler.invoke(null, testMethod, null);
        verify(v1Mock, times(1)).test();
        verify(v2Mock, never()).test();
    }

    @Test
    public void testInvokeUsesFallbackVersion() throws Throwable {
        RequestVersionContext.setVersion(3); // v3 requested, should fall back to v2
        handler.invoke(null, testMethod, null);
        verify(v2Mock, times(1)).test();
        verify(v1Mock, never()).test();
    }

    @Test
    public void testInvokeDefaultsToV1WhenNoVersionSet() throws Throwable {
        RequestVersionContext.clearVersion();
        handler.invoke(null, testMethod, null);
        verify(v1Mock, times(1)).test(); // Defaults to 1
        verify(v2Mock, never()).test();
    }

    @Test
    public void testInvokeThrowsExceptionWhenNoSupportedVersionFound() {
        // Only v2 exists in this scenario
        implementations.clear();
        implementations.put(2, v2Mock);
        handler = new VersionRoutingHandler<>(implementations);

        RequestVersionContext.setVersion(1); // Requesting v1, but only v2 exists. Should fail?
        // Logic says: if requested 1, and only 2 exists, loop 1->1. key(1) not found.
        // throw.

        assertThrows(RuntimeException.class, () -> {
            handler.invoke(null, testMethod, null);
        });
    }
}
