package code.apiversion.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.HashMap;
import java.util.Map;

public class GuiceListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new AppModule(), new ServletModule() {
            @Override
            protected void configureServlets() {
                bind(ServletContainer.class).in(com.google.inject.Singleton.class);

                Map<String, String> params = new HashMap<>();
                params.put("javax.ws.rs.Application", "javax.ws.rs.core.Application");
                params.put("jersey.config.server.provider.packages", "code.apiversion.resources");

                serve("/*").with(ServletContainer.class, params);
            }
        });
    }
}
