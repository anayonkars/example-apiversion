package code.apiversion.core;

public class RequestVersionContext {
    private static final ThreadLocal<Integer> versionContext = new ThreadLocal<>();

    public static void setVersion(Integer version) {
        versionContext.set(version);
    }

    public static Integer getVersion() {
        return versionContext.get();
    }

    public static void clearVersion() {
        versionContext.remove();
    }
}
