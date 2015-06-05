/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.log.Logger;
import io.virga.log.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GAE8415DateFilter implements Filter {
    // https://code.google.com/p/googleappengine/issues/detail?id=8415
    private final static Logger log = LoggerFactory.getLogger(GAE8415DateFilter.class);
    public static boolean active = false;

    static {
        active = System.getProperty("com.google.appengine.runtime.environment", null) != null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (active && log.isInfoEnabled()) {
            log.info("Enabling workaround for GAE bug 8415");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (!active) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace("Wrapping request/response for GAE 8415");
        }
        GAE8415RequestWrapper request = new GAE8415RequestWrapper((HttpServletRequest) servletRequest);
        GAE8415ResponseWrapper response = new GAE8415ResponseWrapper((HttpServletResponse) servletResponse);
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
