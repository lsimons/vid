/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga;

import io.virga.log.Logger;
import io.virga.log.LoggerFactory;

import javax.servlet.FilterConfig;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static boolean DEBUG = true;
    public static boolean TRACE = false;
    public static String VERSION = "0.0.1";
    public static String CLASSPATH_CONFIG_FILE = "vid.properties";
    public static String FILESYSTEM_CONFIG_FILE = "/etc/vid/vid.properties";
    private final static Logger log = LoggerFactory.getLogger(Config.class);

    static {
        loadDefaultConfiguration();
    }

    public static Properties asProperties() {
        Properties properties = new Properties();
        properties.setProperty("vid.version", VERSION);
        properties.setProperty("vid.debug", Boolean.toString(DEBUG));
        properties.setProperty("vid.trace", Boolean.toString(TRACE));
        return properties;
    }

    public static void loadDefaultConfiguration() {
        try (
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        CLASSPATH_CONFIG_FILE)
        ) {
            if (is == null) {
                log.warn("No vid.properties config file found");
            } else {
                Properties properties = new Properties(asProperties());
                try {
                    if (CLASSPATH_CONFIG_FILE.endsWith(".xml")) {
                        properties.loadFromXML(is);
                    } else {
                        properties.load(is);
                    }
                } catch (IOException e) {
                    log.warn("Cannot load properties from vid.properties: " + e.getMessage(), e);
                }
                loadConfiguration(properties);
                loadConfigFile(FILESYSTEM_CONFIG_FILE);
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public static void loadConfiguration(Properties properties) {
        ConfigProperties interpolating;
        if (properties instanceof ConfigProperties) {
            interpolating = (ConfigProperties) properties;
        } else {
            interpolating = new ConfigProperties(properties);
        }
        setDebug(interpolating);
        setTrace(interpolating);
        setVersion(interpolating);
    }

    public static void loadConfiguration(FilterConfig filterConfig) {
        setDebug(filterConfig);
        setTrace(filterConfig);
        setVersion(filterConfig);

        String propertyFileName = filterConfig.getInitParameter("vid.properties.file");
        loadConfigFile(propertyFileName);
        findGAEVersion();
    }

    private static void findGAEVersion() {
        String GAEVersion = System.getProperty("com.google.appengine.application.version", null);
        if (GAEVersion != null && !GAEVersion.isEmpty()) {
            VERSION = VERSION + "-GAE-" + GAEVersion;
        }
    }

    public static void loadConfigFile(String propertyFileName) {
        if (propertyFileName == null) {
            return;
        }
        propertyFileName = propertyFileName.trim();
        if (propertyFileName.isEmpty()) {
            return;
        }
        try {
            ConfigProperties properties = new ConfigProperties();
            File file = new File(propertyFileName);
            if (!file.canRead()) {
                log.warn("Cannot read config file: " + propertyFileName);
                return;
            }
            try (FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis)) {
                if (propertyFileName.endsWith(".xml")) {
                    properties.loadFromXML(bis);
                } else {
                    properties.load(bis);
                }
            }
            loadConfiguration(properties);
        } catch (SecurityException | IOException e) {
            log.error("Cannot read config file: " + e.getMessage(), e);
        }
    }

    private static void setDebug(FilterConfig filterConfig) {
        DEBUG = getInitParamBoolean(filterConfig, "vid.debug", DEBUG);
    }

    private static void setDebug(Properties properties) {
        DEBUG = getPropertyBoolean(properties, "vid.debug", DEBUG);
        DEBUG = getEnvBoolean("DEBUG", DEBUG);
    }

    private static void setTrace(FilterConfig filterConfig) {
        TRACE = getInitParamBoolean(filterConfig, "vid.trace", DEBUG);
    }

    private static void setTrace(Properties properties) {
        TRACE = getPropertyBoolean(properties, "vid.trace", TRACE);
        TRACE = getEnvBoolean("TRACE", TRACE);
    }

    private static void setVersion(FilterConfig filterConfig) {
        VERSION = getInitParamString(filterConfig, "vid.version", VERSION);
    }

    private static void setVersion(Properties properties) {
        VERSION = getPropertyString(properties, "vid.version", VERSION);
        VERSION = getEnvString("VERSION", VERSION);
    }

    private static boolean getEnvBoolean(String name, boolean defaultValue) {
        try {
            String value = System.getenv(name);
            if (value == null) {
                return defaultValue;
            }
            value = value.trim();
            if ("1".equals(value)) {
                return true;
            }
        } catch (SecurityException e) {
            // use default
        }
        return defaultValue;
    }

    private static String getEnvString(String name, String defaultValue) {
        try {
            String value = System.getenv(name);
            if (value == null) {
                return defaultValue;
            }
            value = value.trim();
            if (value.isEmpty()) {
                return defaultValue;
            }
            return value;
        } catch (SecurityException e) {
            // use default
        }
        return defaultValue;
    }

    private static boolean getPropertyBoolean(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private static String getPropertyString(Properties properties, String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    private static boolean getInitParamBoolean(FilterConfig filterConfig, String name, boolean defaultValue) {
        String value = filterConfig.getInitParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    private static String getInitParamString(FilterConfig filterConfig, String name, String defaultValue) {
        String value = filterConfig.getInitParameter(name);
        if (value == null) {
            return defaultValue;
        }
        value = value.trim();
        if (value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    /** Simple helper {@link Properties} that processes foo=${propertyName} constructs. */
    public static class ConfigProperties extends Properties {
        public ConfigProperties() {
        }

        public ConfigProperties(Properties defaults) {
            super(defaults);
        }

        @Override
        public String getProperty(String key) {
            return getProperty(key, 0);
        }

        @Override
        public String getProperty(String key, String defaultValue) {
            return getProperty(key, defaultValue, 0);
        }

        private String getProperty(String key, int level) {
            String value = super.getProperty(key);
            if (value == null || level > 10) {
                return value;
            }
            return interpolate(value, level);
        }

        private String getProperty(String key, String defaultValue, int level) {
            String value = getProperty(key, level);
            if (value == null) {
                return defaultValue;
            }
            return value;
        }

        private String interpolate(String value, int level) {
            int start = value.indexOf("${");
            while (start != -1) {
                int end = value.indexOf("}", start);
                if (end == -1) {
                    return value;
                }

                String variableName = value.substring(start + 2, end);
                String variableValue = getProperty(variableName, level + 1);
                int endOfVariable = end;
                if (variableValue != null) {
                    String beforeVariable = value.substring(0, start);
                    String afterVariable = value.substring(end + 1);
                    value = beforeVariable + variableValue;
                    endOfVariable = value.length();
                    value += afterVariable;
                }
                start = value.indexOf("${", endOfVariable);
            }

            return value;
        }
    }
}
