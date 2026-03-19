package code.apiversion;

import java.util.logging.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import code.apiversion.controller.ExampleController;
import code.apiversion.core.RequestVersionContext;
import code.apiversion.injection.AppModule;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        ExampleController exampleController = injector.getInstance(ExampleController.class);

        LOG.info(exampleController.getExample());
        LOG.info(exampleController.getDetails());

        RequestVersionContext.setVersion(1);
        LOG.info(exampleController.getExample());
        LOG.info(exampleController.getDetails());
        RequestVersionContext.clearVersion();

        RequestVersionContext.setVersion(2);
        LOG.info(exampleController.getExample());
        LOG.info(exampleController.getDetails());
        RequestVersionContext.clearVersion();

        RequestVersionContext.setVersion(3);
        LOG.info(exampleController.getExample());
        LOG.info(exampleController.getDetails());
        RequestVersionContext.clearVersion();

        RequestVersionContext.setVersion(4);
        LOG.info(exampleController.getExample());
        LOG.info(exampleController.getDetails());
        RequestVersionContext.clearVersion();
    }
}