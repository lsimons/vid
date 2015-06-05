/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.servlet;

import io.virga.convert.ConverterFactory;
import io.virga.convert.Serializer;
import io.virga.servlet.GAE8415DateFilter;

import javax.activation.MimeType;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static io.virga.convert.MimeTypes.chooseResponseType;
import static io.virga.convert.MimeTypes.getSupportedResponseTypes;

public abstract class ObjectServlet extends HttpServlet {
    private final static long started = System.currentTimeMillis();

    private static boolean handleConditional(HttpServletRequest request, HttpServletResponse response,
            long lastModified) {
        long ifModifiedSince = getIfModifiedSince(request);
        if (lastModified > 0 && ifModifiedSince != -1 && lastModified <= ifModifiedSince) {
            response.setStatus(304);
            response.setDateHeader("Last-Modified", lastModified);
            return true;
        }
        return false;
    }

    private static long getIfModifiedSince(HttpServletRequest request) {
        if (GAE8415DateFilter.active) {
            return -1L;
        }
        return request.getDateHeader("If-Modified-Since");
    }

    protected <T> void doGetObject(HttpServletRequest request, HttpServletResponse response, T object)
            throws IOException {
        MimeType responseType = chooseResponseType(request);
        Serializer<T> serializer = ConverterFactory.getSerializer(object);

        long lastModified = getLastModified();

        response.setContentType(responseType.toString());
        response.setCharacterEncoding("UTF-8");
        if (lastModified > 0) {
            response.setDateHeader("Last-Modified", lastModified);
        }
        response.setHeader("Cache-Control", getCacheControl());
        response.setHeader("Vary", "Accept");
        linkAlternates(response, responseType);
        if (handleConditional(request, response, lastModified)) {
            return;
        }

        PrintWriter pw = response.getWriter();
        serializer.serialize(object, pw, responseType);
        pw.flush();
    }

    private void linkAlternates(HttpServletResponse response, MimeType responseType) {
        List<MimeType> supportedTypes = getSupportedResponseTypes();
        StringBuffer buf = new StringBuffer();
        for (MimeType mimeType : supportedTypes) {
            if (responseType.match(mimeType)) {
                continue;
            }
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append("<>;rel=alternate;type=" + mimeType.getBaseType());
        }
        if (buf.length() > 0) {
            response.addHeader("Link", buf.toString());
        }
    }

    @SuppressWarnings("SameReturnValue")
    protected String getCacheControl() {
        return "public, max-age=3600";
    }

    protected long getLastModified() {
        return started;
    }
}
