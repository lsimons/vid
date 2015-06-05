/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.servlet;

import io.virga.model.Version;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

@Test(groups = {"functional"})
public class VersionServletTest extends AbstractObjectServletTest {
    @Test
    public void testGetHtml() throws Exception {
        newRequest();
        getResponse();
        assertObjectResponse();
        assertHtml();
        assertContainsVersion();
    }

    @Test(groups = {"checkin"})
    public void testGetJson() throws Exception {
        newRequest();
        acceptJson();
        getResponse();
        assertObjectResponse();
        assertJson();
        parseJson();
        assertContainsVersion();
    }

    @Test
    public void testGetText() throws Exception {
        newRequest();
        acceptText();
        getResponse();
        assertObjectResponse();
        assertText();
        assertContainsVersion();
    }

    @Test
    public void testGetXml() throws Exception {
        newRequest();
        acceptXml();
        getResponse();
        assertObjectResponse();
        assertXml();
        parseXml();
        assertContainsVersion();
    }

    protected void assertContainsVersion() throws IOException {
        String text = response.getText();
        Version version = Version.getSystemVersion();
        String systemVersion = version.getValue();
        assertTrue(text.matches("(?s).*?" + systemVersion.replaceAll(".", "\\.") + ".*"));
    }

    @Override
    protected String getServletClass() {
        return VersionServlet.class.getName();
    }
}
