package code.apiversion.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A dynamic proxy invocation handler that routes method calls to the
 * appropriate
 * implementation based on the API version stored in
 * {@link RequestVersionContext}.
 * <p>
 * It supports version fallback: if the requested version is not found, it tries
 * lower versions until a match is found or fails.
 *
 * @param <T> The interface type being proxied.
 */
public class VersionRoutingHandler<T> implements InvocationHandler {

    private static final Logger LOG = Logger.getLogger(VersionRoutingHandler.class.getName());

    private final Map<Integer, T> implementations;

    public VersionRoutingHandler(Map<Integer, T> implementations) {
        this.implementations = implementations;
    }

    /**
     * Intercepts method calls and delegates them to the implementation matching the
     * requested API version. Falls back to the highest available version that is
     * less than or equal to the requested version.
     *
     * @param proxy  The proxy instance.
     * @param method The method being invoked.
     * @param args   The arguments to the method.
     * @return The result of the method invocation.
     * @throws Throwable If the invoked method throws an exception or no suitable
     *                   version is found.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Integer requestedVersion = RequestVersionContext.getVersion();

        // default to v1 if no version is requested
        int version = requestedVersion == null ? 1 : requestedVersion;

        // Fallback Logic: If V3 requested but only V2 exists, use V2.
        T target = null;
        for (int v = version; v >= 1; v--) {
            target = implementations.get(v);
            if (target != null) {
                LOG.fine(() -> "Routing to version " + v + " (" + target.getClass().getCanonicalName() + ")");
                break;
            }
        }

        if (target == null) {
            throw new RuntimeException("No supported version found for v" + version);
        }

        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
