/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class NotAcceptable extends BadRequest {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Not Acceptable";
    public static final int STATUS_CODE = 406;

    public NotAcceptable() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public NotAcceptable(Throwable t) {
        super(t, STATUS_CODE);
    }

    public NotAcceptable(String message) {
        super(message, STATUS_CODE);
    }

    public NotAcceptable(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public NotAcceptable(int statusCode) {
        super(statusCode);
    }

    public NotAcceptable(String message, int statusCode) {
        super(message, statusCode);
    }

    public NotAcceptable(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public NotAcceptable(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
