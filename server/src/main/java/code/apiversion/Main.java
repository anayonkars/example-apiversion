package code.apiversion;

import com.google.inject.Guice;
import com.google.inject.Injector;

import code.apiversion.controller.ExampleController;
import code.apiversion.core.RequestVersionContext;
import code.apiversion.injection.AppModule;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        ExampleController exampleController = injector.getInstance(ExampleController.class);
        System.out.println(exampleController.getExample());
        System.out.println(exampleController.getDetails());
        RequestVersionContext.setVersion(1);
        System.out.println(exampleController.getExample());
        System.out.println(exampleController.getDetails());
        RequestVersionContext.setVersion(2);
        System.out.println(exampleController.getExample());
        System.out.println(exampleController.getDetails());
        RequestVersionContext.clearVersion();
        RequestVersionContext.setVersion(3);
        System.out.println(exampleController.getExample());
        System.out.println(exampleController.getDetails());
        RequestVersionContext.clearVersion();
        RequestVersionContext.setVersion(4);
        System.out.println(exampleController.getExample());
        System.out.println(exampleController.getDetails());
        RequestVersionContext.clearVersion();
    }
}