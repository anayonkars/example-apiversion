package code.apiversion.injection;

import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class GuiceFeature implements Feature {

    @Context
    private ServletContext servletContext;

    @Inject
    private ServiceLocator serviceLocator;

    @Override
    public boolean configure(FeatureContext context) {
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);

        Injector injector = (Injector) servletContext.getAttribute(Injector.class.getName());
        if (injector == null) {
            throw new RuntimeException(
                    "Guice Injector not found in ServletContext attribute: " + Injector.class.getName());
        }
        guiceBridge.bridgeGuiceInjector(injector);
        return true;
    }
}
