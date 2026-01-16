package code.apiversion.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class VersionRoutingHandler<T> implements InvocationHandler {

    private final Map<Integer, T> implementations;

    public VersionRoutingHandler(Map<Integer, T> implementations) {
        this.implementations = implementations;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Integer requestedVersion = RequestVersionContext.getVersion();

        // default to v1 if no version is requested
        requestedVersion = requestedVersion == null ? 1 : requestedVersion;

        // Fallback Logic: If V3 requested but only V2 exists, use V2.
        T target = null;
        System.out.println("Requested version: " + requestedVersion);
        for (int v = requestedVersion; v >= 1; v--) {
            if (implementations.containsKey(v)) {
                target = implementations.get(v);
                System.out.println("Implementation for version " + v + " is " + target.getClass().getCanonicalName());
                break;
            }
            System.out.println("Implementation for version " + v + " not found");
        }

        if (target == null) {
            throw new RuntimeException("No supported version found for v" + requestedVersion);
        }

        return method.invoke(target, args);
    }
}
