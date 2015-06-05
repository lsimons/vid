/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga;

import org.testng.annotations.Test;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;

import static org.testng.Assert.assertEquals;

@Test(groups = {"integration"}, singleThreaded = true)
public class ConfigTest {
    public void testDefaultConfigLoading() throws Exception {
        String defaultClassPathConfigFile = Config.CLASSPATH_CONFIG_FILE;
        String defaultFilesystemConfigFile = Config.FILESYSTEM_CONFIG_FILE;
        String defaultVersion = Config.VERSION;
        File configFile = File.createTempFile("vid-unit-test", ".config.properties");
        try (
                FileOutputStream fos = new FileOutputStream(configFile);
                PrintStream ps = new PrintStream(new BufferedOutputStream(fos));
        ) {
            ps.println("vid.version=TEST2");
            ps.flush();
            ps.close();

            // test loading config from file
            Config.loadDefaultConfiguration();
            Config.FILESYSTEM_CONFIG_FILE = configFile.getAbsolutePath();
            Config.loadDefaultConfiguration();
            assertEquals(Config.VERSION, "TEST2");

            // test re-loading config without config file
            Config.VERSION = "TEST3";
            configFile.delete();
            Config.loadDefaultConfiguration();
            assertEquals(Config.VERSION, "TEST3");

            // test loading config from classpath
            Config.CLASSPATH_CONFIG_FILE = "io/virga/config-test.properties";
            Config.loadDefaultConfiguration();
            assertEquals(Config.VERSION, "TEST1");

            // test re-loading config without properties file
            Config.VERSION = "TEST4";
            Config.CLASSPATH_CONFIG_FILE = "config-no-such-file.properties";
            Config.loadDefaultConfiguration();
            assertEquals(Config.VERSION, "TEST4");

            // test loading config file with embedded ${} variables
            Config.CLASSPATH_CONFIG_FILE = "io/virga/config-test-interpolation.properties";
            Config.loadDefaultConfiguration();
            assertEquals(Config.VERSION, "FOO ${bar} blah <<something like tango>>");
            Config.CLASSPATH_CONFIG_FILE = "io/virga/config-test-interpolation-recursion.properties";
            Config.loadDefaultConfiguration();
            assertEquals(Config.VERSION, "${level12}");

            // test loading config from XML
            Config.CLASSPATH_CONFIG_FILE = "io/virga/config-test-xml.xml";
            Config.loadDefaultConfiguration();
            assertEquals(Config.VERSION, "TEST42");
        } finally {
            if (configFile.exists()) {
                configFile.delete();
            }
            Config.CLASSPATH_CONFIG_FILE = defaultClassPathConfigFile;
            Config.FILESYSTEM_CONFIG_FILE = defaultFilesystemConfigFile;
            Config.VERSION = defaultVersion;
            Config.loadDefaultConfiguration();
        }
    }

    public void testFilterConfigLoading() throws Exception {
        String defaultVersion = Config.VERSION;
        boolean defaultDebug = Config.DEBUG;
        boolean defaultTrace = Config.TRACE;
        String gaeVersion = System.getProperty("com.google.appengine.application.version", "");
        try {
            FilterConfig config = new FilterConfig() {
                @Override
                public String getFilterName() {
                    return "unittest";
                }

                @Override
                public ServletContext getServletContext() {
                    return null;
                }

                @Override
                public String getInitParameter(String s) {
                    return "foo";
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return null;
                }
            };
            Config.loadConfiguration(config);
            assertEquals(Config.VERSION, "foo");
            assertEquals(Config.DEBUG, false);
            assertEquals(Config.TRACE, false);

            config = new FilterConfig() {
                @Override
                public String getFilterName() {
                    return "unittest";
                }

                @Override
                public ServletContext getServletContext() {
                    return null;
                }

                @Override
                public String getInitParameter(String s) {
                    return null;
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return null;
                }
            };
            Config.DEBUG = true;
            Config.TRACE = true;
            Config.VERSION = "bar";
            System.setProperty("com.google.appengine.application.version", "DANCE");
            Config.loadConfiguration(config);
            assertEquals(Config.DEBUG, true);
            assertEquals(Config.TRACE, true);
            assertEquals(Config.VERSION, "bar-GAE-DANCE");
        } finally {
            Config.VERSION = defaultVersion;
            Config.DEBUG = defaultDebug;
            Config.TRACE = defaultTrace;
            System.setProperty("com.google.appengine.application.version", gaeVersion);
        }
    }
}
