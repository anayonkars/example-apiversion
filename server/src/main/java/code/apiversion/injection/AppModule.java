package code.apiversion.injection;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;

import code.apiversion.controller.ExampleController;
import code.apiversion.core.VersionedProxyProvider;
import code.apiversion.service.ExampleService;
import code.apiversion.service.ExampleServiceImplV1;
import code.apiversion.service.ExampleServiceImplV2;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bindVersionedService(ExampleService.class)
                .with(1, new ExampleServiceImplV1())
                .with(2, new ExampleServiceImplV2())
                .build();
    }

    private <T> VersionBinder<T> bindVersionedService(Class<T> clazz) {
        return new VersionBinder<>(clazz);
    }

    private class VersionBinder<T> {
        private Class<T> clazz;
        private Map<Integer, T> map = new HashMap<>();

        public VersionBinder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public VersionBinder<T> with(int version, T impl) {
            map.put(version, impl);
            return this;
        }

        public void build() {
            bind(clazz).toProvider(new VersionedProxyProvider<>(clazz, map));
        }
    }
}
