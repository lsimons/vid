/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.exception.InternalServerError;
import io.virga.exception.NotImplemented;
import org.testng.annotations.Test;

@Test(groups = {"integration"}, singleThreaded = true)
public class ExceptionServletTest extends AbstractExceptionTest {
    @Override
    protected String getWebXml() {
        return "io/virga/servlet/exception-servlet-web.xml";
    }

    @Override
    @Test(expectedExceptions = {NotImplemented.class})
    public void testGetHtml() throws Exception {
        super.testGetHtml();
    }

    @Override
    @Test(expectedExceptions = {NotImplemented.class})
    public void testGetJson() throws Exception {
        super.testGetJson();
    }

    @Override
    @Test(expectedExceptions = {NotImplemented.class})
    public void testGetText() throws Exception {
        super.testGetText();
    }

    @Override
    @Test(expectedExceptions = {NotImplemented.class})
    public void testGetXml() throws Exception {
        super.testGetXml();
    }

    @Override
    @Test(expectedExceptions = {InternalServerError.class})
    public void testCommittedResponseError() throws Exception {
        super.testCommittedResponseError();
    }
}
