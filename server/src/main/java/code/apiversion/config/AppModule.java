package code.apiversion.config;

import com.google.inject.AbstractModule;
import code.apiversion.resources.ExampleController;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExampleController.class);
    }
}
