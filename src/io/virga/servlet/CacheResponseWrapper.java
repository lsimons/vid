/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.convert.HttpDate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CacheResponseWrapper extends HttpServletResponseWrapper {
    private List<Header> interceptedHeaders = new ArrayList<>();
    private boolean intercepting = true;
    private boolean disabled = false;
    private List<String> headersToIntercept = Arrays.asList("Cache-Control", "Expires", "ETag", "Last-Modified");

    public CacheResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    private boolean intercept(String name, long date) {
        if (!intercepting) {
            return false;
        }
        if (headersToIntercept.contains(name)) {
            String value = HttpDate.format(date);
            Header header = new Header(name, value);
            interceptedHeaders.add(header);
            return true;
        }
        return false;
    }

    private boolean intercept(String name, String value) {
        if (!intercepting) {
            return false;
        }
        if (headersToIntercept.contains(name)) {
            Header header = new Header(name, value);
            interceptedHeaders.add(header);
            return true;
        }
        return false;
    }

    private boolean intercept(String name, int value) {
        if (!intercepting) {
            return false;
        }
        if (headersToIntercept.contains(name)) {
            Header header = new Header(name, "" + value);
            interceptedHeaders.add(header);
            return true;
        }
        return false;
    }

    private boolean intercepted(String name) {
        for (Header header : interceptedHeaders) {
            boolean match = header.getName() == null ? name == null : header.getName().equalsIgnoreCase(name);
            if (match) {
                return true;
            }
        }
        return false;
    }

    private void flushHeaders() {
        intercepting = false;
        if (!disabled) {
            for (Header header : interceptedHeaders) {
                super.addHeader(header.getName(), header.getValue());
            }
            interceptedHeaders.clear();
        }
    }

    public void disableCacheHeaders() {
        disabled = true;
        intercepting = false;
        interceptedHeaders.clear();
    }

    @Override
    public void setDateHeader(String name, long date) {
        if (intercept(name, date)) {
            return;
        }
        super.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        if (intercept(name, date)) {
            return;
        }
        super.addDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        if (intercept(name, value)) {
            return;
        }
        super.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        if (intercept(name, value)) {
            return;
        }
        super.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        if (intercept(name, value)) {
            return;
        }
        super.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        if (intercept(name, value)) {
            return;
        }
        super.addIntHeader(name, value);
    }

    @Override
    public boolean containsHeader(String name) {
        return super.containsHeader(name) || intercepted(name);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        flushHeaders();
        return super.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        flushHeaders();
        return super.getWriter();
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        flushHeaders();
        super.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        flushHeaders();
        super.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        flushHeaders();
        super.sendRedirect(location);
    }

    @Override
    public void flushBuffer() throws IOException {
        flushHeaders();
        super.flushBuffer();
    }
}
