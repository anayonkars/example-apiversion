package code.apiversion.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExampleServiceImplV1Test {

    private final ExampleServiceImplV1 service = new ExampleServiceImplV1();

    @Test
    public void testGetGreeting() {
        assertEquals("Getting greeting from Service Layer v1.", service.getGreeting());
    }

    @Test
    public void testGetDetailsContainsV1AndClassName() {
        String result = service.getDetails();
        assertTrue(result.contains("v1"));
        assertTrue(result.contains(ExampleServiceImplV1.class.getCanonicalName()));
    }
}
