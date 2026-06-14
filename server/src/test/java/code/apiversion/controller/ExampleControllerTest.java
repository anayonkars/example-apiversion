package code.apiversion.controller;

import code.apiversion.service.ExampleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExampleControllerTest {

    private ExampleService mockService;
    private ExampleController controller;

    @BeforeEach
    public void setUp() {
        mockService = mock(ExampleService.class);
        controller = new ExampleController(mockService);
    }

    @Test
    public void testGetExampleDelegatesToService() {
        when(mockService.getGreeting()).thenReturn("hello");

        String result = controller.getExample();

        assertEquals("hello", result);
        verify(mockService).getGreeting();
    }

    @Test
    public void testGetDetailsDelegatesToService() {
        when(mockService.getDetails()).thenReturn("some details");

        String result = controller.getDetails();

        assertEquals("some details", result);
        verify(mockService).getDetails();
    }
}
