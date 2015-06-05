/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.servlet;

import io.virga.model.Link;
import io.virga.model.LinkSection;
import io.virga.model.Page;
import io.virga.vid.serializers.PageSerializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class PageServlet extends ObjectServlet {
    static {
        PageSerializer.load();
    }

    public static LinkSection menu(HttpServletRequest request) {
        return new LinkSection("Menu",
                new Link("Home", request.getContextPath() + "/"),
                new Link("About", request.getContextPath() + "/about"),
                new Link("Version", request.getContextPath() + "/version"));
    }

    protected abstract Page getPage(HttpServletRequest request, HttpServletResponse response);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Page page = getPage(request, response);
        doGetObject(request, response, page);
    }
}
