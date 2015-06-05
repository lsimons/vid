/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.convert.HttpDate;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class GAE8415ResponseWrapper extends HttpServletResponseWrapper {
    private List headers = new ArrayList();

    public GAE8415ResponseWrapper(HttpServletResponse response) {
        super(response);

// Disabled because GAE is on old javax.servlet
//        final Collection values = response.getHeaderNames();
//        final List filtered = new ArrayList();
//        for (Object o : values) {
//            String value = (String) o;
//            if (shouldFilter(value)) {
//                continue;
//            }
//            filtered.add(value);
//        }
//        headers = filtered;
    }

    @Override
    public String getHeader(String name) {
        if (shouldFilter(name)) {
            return null;
        }
        return super.getHeader(name);
    }

    @Override
    public Collection getHeaders(String name) {
        if (shouldFilter(name)) {
            return Collections.emptyList();
        }
        return super.getHeaders(name);
    }

// Disabled because GAE is on old javax.servlet
//    @Override
//    public Collection getHeaderNames() {
//        return headers;
//    }

    @Override
    public void setHeader(String name, String value) {
        if (shouldFilter(name)) {
            return;
        }
        super.setHeader(name, value);
        if (!headers.contains(name)) {
            headers.add(name);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        if (shouldFilter(name)) {
            return;
        }
        super.addHeader(name, value);
        if (!headers.contains(name)) {
            headers.add(name);
        }
    }

    @Override
    public void setIntHeader(String name, int value) {
        if (shouldFilter(name)) {
            return;
        }
        super.setIntHeader(name, value);
        if (!headers.contains(name)) {
            headers.add(name);
        }
    }

    @Override
    public void addIntHeader(String name, int value) {
        if (shouldFilter(name)) {
            return;
        }
        super.addIntHeader(name, value);
        if (!headers.contains(name)) {
            headers.add(name);
        }
    }

    @Override
    public void setDateHeader(String name, long date) {
        if (shouldFilter(name)) {
            return;
        }
        String value = HttpDate.format(date);
        setHeader(name, value);
    }

    @Override
    public void addDateHeader(String name, long date) {
        if (shouldFilter(name)) {
            return;
        }
        String value = HttpDate.format(date);
        addHeader(name, value);
    }

    private boolean shouldFilter(String name) {
        return "If-Modified-Since".equalsIgnoreCase(name);
    }
}
