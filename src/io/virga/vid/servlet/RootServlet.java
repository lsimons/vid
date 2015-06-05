/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.servlet;

import io.virga.model.Page;
import io.virga.model.Section;
import io.virga.model.TextSection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RootServlet extends PageServlet {
    @Override
    protected Page getPage(HttpServletRequest request, HttpServletResponse response) {
        return new Page("VID", menu(request), about());
    }

    private Section about() {
        return new TextSection("About", "VID is an application that currently does nothing.");
    }
}
