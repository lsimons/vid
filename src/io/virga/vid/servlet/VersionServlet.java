/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.servlet;

import io.virga.model.Version;
import io.virga.vid.serializers.VersionSerializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class VersionServlet extends ObjectServlet {
    static {
        VersionSerializer.load();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGetObject(request, response, Version.getSystemVersion());
    }
}
