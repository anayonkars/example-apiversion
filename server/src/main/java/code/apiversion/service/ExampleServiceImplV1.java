package code.apiversion.service;

public class ExampleServiceImplV1 implements ExampleService {
    @Override
    public String getGreeting() {
        return "Getting greeting from Service Layer v1.";
    }

    @Override
    public String getDetails() {
        return "Getting details from Service Layer v1. This is " + this.getClass().getCanonicalName();
    }
}
