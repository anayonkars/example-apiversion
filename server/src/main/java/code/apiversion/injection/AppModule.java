package code.apiversion.injection;

import com.google.inject.AbstractModule;

import code.apiversion.controller.ExampleController;
import code.apiversion.service.ExampleService;
import code.apiversion.service.ExampleServiceImplV1;
import code.apiversion.service.ExampleServiceImplV2;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExampleController.class);
        bind(ExampleService.class).to(ExampleServiceImplV1.class);
        // bind(ExampleService.class).to(ExampleServiceImplV2.class);
    }
}
