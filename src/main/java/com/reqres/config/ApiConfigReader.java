package com.reqres.config;

import java.io.InputStream;
import java.util.Properties;

public class ApiConfigReader {
    private static Properties properties;

    static {
        try (InputStream is = ApiConfigReader.class.getClassLoader().getResourceAsStream("api-config.properties")) {
            properties = new Properties();
            if (is != null) {
                properties.load(is);
            } else {
                throw new RuntimeException("api-config.properties not found in classpath");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load api-config.properties", e);
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("base.url", "https://reqres.in/api");
    }

    public static String getApiKey() {
        return properties.getProperty("api.key", "free_user_3EkhZVOdslRsAMltjILEG1D2FnU");
    }
}
