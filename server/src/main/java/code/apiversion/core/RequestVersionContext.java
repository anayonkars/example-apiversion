package code.apiversion.core;

/**
 * Holds the API version for the current request using {@link ThreadLocal}.
 * This context allows the version to be accessed anywhere during the request
 * processing.
 */
public class RequestVersionContext {
    private static final ThreadLocal<Integer> versionContext = new ThreadLocal<>();

    /**
     * Sets the API version for the current thread.
     * 
     * @param version The version number.
     */
    public static void setVersion(Integer version) {
        versionContext.set(version);
    }

    /**
     * Gets the API version for the current thread.
     * 
     * @return The version number, or null if not set.
     */
    public static Integer getVersion() {
        return versionContext.get();
    }

    /**
     * Clears the API version from the current thread.
     * Should be called at the end of the request to prevent memory leaks.
     */
    public static void clearVersion() {
        versionContext.remove();
    }
}
