/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import org.testng.annotations.Test;

@Test(groups = {"integration"}, singleThreaded = true)
public class ExceptionFilterTest extends AbstractExceptionTest {
    @Override
    protected String getWebXml() {
        return "io/virga/servlet/exception-filter-web.xml";
    }
}
