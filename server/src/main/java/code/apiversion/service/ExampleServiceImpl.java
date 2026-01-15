package code.apiversion.service;

public class ExampleServiceImpl implements ExampleService {
    @Override
    public String getGreeting() {
        return "Hello from Service Layer with Guice!";
    }
}
