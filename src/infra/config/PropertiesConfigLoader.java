package infra.config;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfigLoader implements ConfigLoader {

    private final Properties props = new Properties();

    public PropertiesConfigLoader(String fileName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) throw new IllegalArgumentException("Config file not found: " + fileName);
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + fileName, e);
        }
    }

    @Override
    public String get(String key) {
        String value = props.getProperty(key);
        if (value == null) throw new IllegalArgumentException("Missing required config key: " + key);
        return value.trim();
    }

    @Override
    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue).trim();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        return value == null ? defaultValue : Integer.parseInt(value.trim());
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = props.getProperty(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.trim());
    }
}
