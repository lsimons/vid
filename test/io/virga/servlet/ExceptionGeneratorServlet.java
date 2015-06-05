/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.exception.NotImplemented;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionGeneratorServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NotImplemented notImplemented = new NotImplemented();
        notImplemented.setDetail("this is a unit test");
        throw notImplemented;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NotImplemented notImplemented = new NotImplemented();
        notImplemented.setDetail("this is a unit test");
        throw notImplemented;
    }
}
