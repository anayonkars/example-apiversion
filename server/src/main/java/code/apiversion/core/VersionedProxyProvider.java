package code.apiversion.core;

import java.lang.reflect.Proxy;
import java.util.Map;

import com.google.inject.Provider;

public class VersionedProxyProvider<T> implements Provider<T> {
    private final Class<T> interfaceType;
    private final Map<Integer, T> implementations;

    public VersionedProxyProvider(Class<T> interfaceType, Map<Integer, T> implementations) {
        this.interfaceType = interfaceType;
        this.implementations = implementations;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[] { interfaceType },
                new VersionRoutingHandler<>(implementations));
    }
}
