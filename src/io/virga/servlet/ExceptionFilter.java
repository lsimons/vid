/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionFilter implements Filter {
    static {
        ErrorMessageSerializer.load();
    }

    private final ExceptionHandler exceptionHandler = new ExceptionHandler();
    private boolean filterCacheHeaders = true;

    public void init(FilterConfig config) throws ServletException {
        String showStackTraces = config.getInitParameter("showStackTraces");
        if (showStackTraces != null) {
            exceptionHandler.setShowStackTraces(Boolean.parseBoolean(showStackTraces));
        }
        String filterCacheHeaders = config.getInitParameter("filterCacheHeaders");
        if (filterCacheHeaders != null) {
            this.filterCacheHeaders = Boolean.parseBoolean(filterCacheHeaders);
        }
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (filterCacheHeaders) {
            res = new CacheResponseWrapper((HttpServletResponse) response);
        }

        try {
            chain.doFilter(req, res);
        } catch (ServletException | IOException | RuntimeException e) {
            // one imagines that servlet engines will ensure we do not cache the errors, but since in the end we are
            // not calling sendError() or similar there's really no guarantee.
            if (filterCacheHeaders) {
                CacheResponseWrapper crw = (CacheResponseWrapper) res;
                crw.disableCacheHeaders();
            }
            exceptionHandler.handleException(req, res, e);
        }
    }
}
