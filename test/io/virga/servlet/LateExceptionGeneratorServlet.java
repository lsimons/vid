/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.exception.InternalServerError;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LateExceptionGeneratorServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        startResponse(response);
        throwException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        startResponse(response);
        throwException();
    }

    private void startResponse(HttpServletResponse response) throws IOException {
        response.getWriter().println("<html><head><title>Late exception</title></head><body>");
        response.getWriter().println("writing response...");
    }

    private void throwException() throws InternalServerError {
        InternalServerError e = new InternalServerError("unit test", 599);
        throw e;
    }
}
