package code.apiversion.injection;

import com.google.inject.AbstractModule;

import code.apiversion.controller.ExampleController;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExampleController.class);
    }
}
