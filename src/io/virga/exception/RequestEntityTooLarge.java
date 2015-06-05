/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class RequestEntityTooLarge extends BadRequest {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Request Entity Too Large";
    public static final int STATUS_CODE = 413;

    public RequestEntityTooLarge() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public RequestEntityTooLarge(Throwable t) {
        super(t, STATUS_CODE);
    }

    public RequestEntityTooLarge(String message) {
        super(message, STATUS_CODE);
    }

    public RequestEntityTooLarge(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public RequestEntityTooLarge(int statusCode) {
        super(statusCode);
    }

    public RequestEntityTooLarge(String message, int statusCode) {
        super(message, statusCode);
    }

    public RequestEntityTooLarge(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public RequestEntityTooLarge(Throwable t, int statusCode) {
        super(t, statusCode);
    }

    public static RequestEntityTooLarge forSize(final int size) {
        return new RequestEntityTooLarge("Value of payload is too large:" + size + " bytes", STATUS_CODE);
    }

    public static void throwForSize(final int size) throws RequestEntityTooLarge {
        throw forSize(size);
    }
}
