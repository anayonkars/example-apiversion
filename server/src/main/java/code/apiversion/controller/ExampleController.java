package code.apiversion.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import code.apiversion.service.ExampleService;

@Path("/example")
public class ExampleController {

    private final ExampleService exampleService;

    @Inject
    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getExample() {
        return exampleService.getGreeting();
    }
}
