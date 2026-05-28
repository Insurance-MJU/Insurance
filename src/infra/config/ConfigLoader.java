package infra.config;

public interface ConfigLoader {
    String get(String key);
    String get(String key, String defaultValue);
    int getInt(String key, int defaultValue);
    boolean getBoolean(String key, boolean defaultValue);
}
