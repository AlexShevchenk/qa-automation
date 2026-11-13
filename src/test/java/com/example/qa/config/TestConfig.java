package com.example.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Central place for test settings.
 * Resolution order: -D system property -> config.properties.
 */
public final class TestConfig {

    private static final Properties PROPS = load();

    private TestConfig() {
    }

    public static String uiBaseUrl() {
        return get("ui.base.url");
    }

    public static String username() {
        return get("ui.username");
    }

    public static String password() {
        return get("ui.password");
    }

    public static String apiBaseUrl() {
        return get("api.base.url");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static String browserSize() {
        return get("browser.size");
    }

    public static long timeoutMs() {
        return Long.parseLong(get("timeout.ms"));
    }

    public static int apiTimeoutMs() {
        return Integer.parseInt(get("api.timeout.ms"));
    }

    /** Browser binary, e.g. /usr/bin/chromium inside Docker. Empty = Selenium auto-detects Chrome. */
    public static Optional<String> chromeBinary() {
        return Optional.ofNullable(System.getenv("CHROME_BIN")).filter(s -> !s.isBlank());
    }

    /** Pre-installed chromedriver, used inside Docker. Empty = Selenium Manager resolves it. */
    public static Optional<String> chromeDriverBinary() {
        return Optional.ofNullable(System.getenv("CHROMEDRIVER_BIN")).filter(s -> !s.isBlank());
    }

    private static String get(String key) {
        String override = System.getProperty(key);
        return override != null ? override : PROPS.getProperty(key);
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = TestConfig.class.getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config.properties", e);
        }
        return props;
    }
}
