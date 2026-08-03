package code.apiversion.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.ws.rs.NotFoundException;

/**
 * A dynamic proxy invocation handler that routes method calls to the
 * appropriate
 * implementation based on the API version stored in
 * {@link RequestVersionContext}.
 * <p>
 * It supports version fallback: if the requested version is not registered, the
 * call is routed to the highest registered version below it.
 *
 * @param <T> The interface type being proxied.
 */
public class VersionRoutingHandler<T> implements InvocationHandler {

    private static final Logger LOG = Logger.getLogger(VersionRoutingHandler.class.getName());

    /** Version assumed when a request carries no version prefix. */
    static final int DEFAULT_VERSION = 1;

    private final NavigableMap<Integer, T> implementations;

    public VersionRoutingHandler(Map<Integer, T> implementations) {
        this.implementations = Collections.unmodifiableNavigableMap(new TreeMap<>(implementations));
    }

    /**
     * Intercepts method calls and delegates them to the implementation matching the
     * requested API version. Falls back to the highest available version that is
     * less than or equal to the requested version.
     * <p>
     * Resolution is a {@link NavigableMap#floorEntry} lookup rather than a
     * decrementing scan: the requested version is attacker-controlled, so a scan
     * would let a request such as {@code /v2147483647/greeting} pin a CPU core for
     * seconds.
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
        int version = requestedVersion == null ? DEFAULT_VERSION : requestedVersion;

        Map.Entry<Integer, T> resolved = implementations.floorEntry(version);
        if (resolved == null) {
            throw new NotFoundException("No supported version found for v" + version);
        }

        LOG.fine(() -> "Routing to version " + resolved.getKey() + " ("
                + resolved.getValue().getClass().getCanonicalName() + ")");

        try {
            return method.invoke(resolved.getValue(), args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
