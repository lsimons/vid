/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class NotFound extends BadRequest {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Not Found";
    public static final int STATUS_CODE = 404;

    public NotFound() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public NotFound(Throwable t) {
        super(t, STATUS_CODE);
    }

    public NotFound(String message) {
        super(message, STATUS_CODE);
    }

    public NotFound(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public NotFound(int statusCode) {
        super(statusCode);
    }

    public NotFound(String message, int statusCode) {
        super(message, statusCode);
    }

    public NotFound(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public NotFound(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
