package code.apiversion.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}
