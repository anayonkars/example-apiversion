package code.apiversion.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicReference;

public class RequestVersionContextTest {

    @AfterEach
    public void tearDown() {
        RequestVersionContext.clearVersion();
    }

    @Test
    public void testSetAndGetVersion() {
        RequestVersionContext.setVersion(2);
        Integer version = RequestVersionContext.getVersion();
        assertNotNull(version);
        assertEquals(2, version.intValue());
    }

    @Test
    public void testClearVersion() {
        RequestVersionContext.setVersion(3);
        RequestVersionContext.clearVersion();
        Integer version = RequestVersionContext.getVersion();
        assertNull(version);
    }

    @Test
    public void testGetVersionWhenNotSet() {
        Integer version = RequestVersionContext.getVersion();
        assertNull(version);
    }

    @Test
    public void testThreadIsolation() throws InterruptedException {
        RequestVersionContext.setVersion(99);

        AtomicReference<Integer> threadVersion = new AtomicReference<>();
        Thread t = new Thread(() -> threadVersion.set(RequestVersionContext.getVersion()));
        t.start();
        t.join();

        assertNull(threadVersion.get(), "Other thread must not inherit caller's version");
        assertEquals(99, RequestVersionContext.getVersion(), "Caller's version must be unaffected");
    }
}
