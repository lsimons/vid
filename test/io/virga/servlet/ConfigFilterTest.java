/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.servletunit.ServletRunner;
import io.virga.AbstractServletTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.Assert.assertTrue;

@Test(groups = {"integration"}, singleThreaded = true)
public class ConfigFilterTest extends AbstractServletTest {
    @BeforeMethod(alwaysRun = true)
    public void createServlet() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "io/virga/servlet/config-filter-web.xml");
        sr = new ServletRunner(is);
        sc = sr.newClient();
        sc.setExceptionsThrownOnErrorStatus(false);
        newRequest();
        System.out.flush();
        System.err.flush();
    }

    @BeforeClass(alwaysRun = true)
    public void disableJavascript() {
        HttpUnitOptions.setScriptingEnabled(false);
    }

    @AfterMethod(alwaysRun = true)
    public void flush() {
        System.out.flush();
        System.err.flush();
    }

    @Test
    public void testGetHtml() throws Exception {
        getResponse();
        String text = response.getText().toLowerCase();
        assertTrue(text.contains("unittest1"));
    }
}
