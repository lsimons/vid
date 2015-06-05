/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.servlet;

import com.meterware.servletunit.ServletRunner;
import io.virga.AbstractServletTest;
import io.virga.exception.BadRequest;
import io.virga.exception.NotAcceptable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public abstract class AbstractObjectServletTest extends AbstractServletTest {
    protected abstract String getServletClass();

    @BeforeMethod(alwaysRun = true)
    public void createServlet() {
        sr = new ServletRunner();
        sr.registerServlet("", getServletClass());
        sc = sr.newClient();
        sc.setExceptionsThrownOnErrorStatus(false);
        newRequest();
        System.out.flush();
        System.err.flush();
    }

    @Test
    public void getInvalidType() throws Exception {
        request.setHeaderField("Accept", "not-a-mime-type");
        String text;
        try {
            response = sc.getResponse(request);
            assertEquals(400, response.getResponseCode());
            text = response.getText();
        } catch (BadRequest e) {
            text = e.getMessage();
        }
        text = text.toLowerCase();
        assertTrue(text.contains("accept"));
    }

    @Test
    public void getUnacceptableType() throws Exception {
        request.setHeaderField("Accept", "application/x-unknown-type");
        String text;
        try {
            response = sc.getResponse(request);
            assertEquals(406, response.getResponseCode());
            text = response.getText();
        } catch (NotAcceptable e) {
            text = e.getMessage();
        }
        text = text.toLowerCase();
        assertTrue(text.contains("not"));
        assertTrue(text.contains("acceptable"));
    }
}
