package code.apiversion.injection;

import com.google.inject.AbstractModule;

import code.apiversion.controller.ExampleController;
import code.apiversion.service.ExampleService;
import code.apiversion.service.ExampleServiceImpl;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExampleController.class);
        bind(ExampleService.class).to(ExampleServiceImpl.class);
    }
}
