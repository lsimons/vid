/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class NotImplemented extends InternalServerError {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Not Implemented";
    public static final int STATUS_CODE = 501;

    public NotImplemented() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public NotImplemented(Throwable t) {
        super(t, STATUS_CODE);
    }

    public NotImplemented(String message) {
        super(message, STATUS_CODE);
    }

    public NotImplemented(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public NotImplemented(int statusCode) {
        super(statusCode);
    }

    public NotImplemented(String message, int statusCode) {
        super(message, statusCode);
    }

    public NotImplemented(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public NotImplemented(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
