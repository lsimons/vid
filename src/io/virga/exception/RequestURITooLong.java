/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class RequestURITooLong extends BadRequest {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Request URI Too Long";
    public static final int STATUS_CODE = 415;

    public RequestURITooLong() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public RequestURITooLong(Throwable t) {
        super(t, STATUS_CODE);
    }

    public RequestURITooLong(String message) {
        super(message, STATUS_CODE);
    }

    public RequestURITooLong(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public RequestURITooLong(int statusCode) {
        super(statusCode);
    }

    public RequestURITooLong(String message, int statusCode) {
        super(message, statusCode);
    }

    public RequestURITooLong(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public RequestURITooLong(Throwable t, int statusCode) {
        super(t, statusCode);
    }

    public static RequestURITooLong forSize(final int size) {
        return new RequestURITooLong("Length of request URI is too long:" + size + " characters", STATUS_CODE);
    }

    public static void throwForSize(final int size) throws RequestURITooLong {
        throw forSize(size);
    }
}
