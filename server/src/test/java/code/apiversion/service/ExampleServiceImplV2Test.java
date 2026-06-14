package code.apiversion.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExampleServiceImplV2Test {

    private final ExampleServiceImplV2 service = new ExampleServiceImplV2();

    @Test
    public void testGetGreeting() {
        assertEquals("Getting greeting from Service Layer v2", service.getGreeting());
    }

    @Test
    public void testGetDetailsContainsV2AndClassName() {
        String result = service.getDetails();
        assertTrue(result.contains("v2"));
        assertTrue(result.contains(ExampleServiceImplV2.class.getCanonicalName()));
    }
}
