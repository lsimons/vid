/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.convert.HttpDate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@SuppressWarnings("unchecked")
public class GAE8415RequestWrapper extends HttpServletRequestWrapper {
    private List headers = new ArrayList();

    public GAE8415RequestWrapper(HttpServletRequest request) {
        super(request);

        final Enumeration values = super.getHeaderNames();
        final List filtered = new ArrayList();
        while (values.hasMoreElements()) {
            Object o = values.nextElement();
            String value = (String) o;
            if (shouldFilter(value)) {
                continue;
            }
            filtered.add(value);
        }
        headers = filtered;
    }

    @Override
    public String getHeader(String name) {
        if (shouldFilter(name)) {
            return null;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration getHeaderNames() {
        return Collections.enumeration(headers);
    }

    @Override
    public long getDateHeader(String name) {
        String value = getHeader(name);
        if (value == null) {
            return -1L;
        }
        return HttpDate.parse(value);
    }

    @Override
    public int getIntHeader(String name) {
        if (shouldFilter(name)) {
            return -1;
        }
        return super.getIntHeader(name);
    }

    private boolean shouldFilter(String name) {
        return "If-Modified-Since".equalsIgnoreCase(name);
    }
}
