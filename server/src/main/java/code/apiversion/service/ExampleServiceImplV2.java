package code.apiversion.service;

public class ExampleServiceImplV2 implements ExampleService {
    @Override
    public String getGreeting() {
        return "Getting greeting from Service Layer v2";
    }

    @Override
    public String getDetails() {
        return "Getting details from Service Layer v2. This is " + this.getClass().getCanonicalName();
    }
}
