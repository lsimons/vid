/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class Forbidden extends BadRequest {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Forbidden";
    public static final int STATUS_CODE = 403;

    public Forbidden() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public Forbidden(Throwable t) {
        super(t, STATUS_CODE);
    }

    public Forbidden(String message) {
        super(message, STATUS_CODE);
    }

    public Forbidden(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public Forbidden(int statusCode) {
        super(statusCode);
    }

    public Forbidden(String message, int statusCode) {
        super(message, statusCode);
    }

    public Forbidden(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public Forbidden(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
