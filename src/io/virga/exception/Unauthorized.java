/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class Unauthorized extends BadRequest {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Unauthorized";
    public static final int STATUS_CODE = 401;

    public Unauthorized() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public Unauthorized(Throwable t) {
        super(t, STATUS_CODE);
    }

    public Unauthorized(String message) {
        super(message, STATUS_CODE);
    }

    public Unauthorized(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public Unauthorized(int statusCode) {
        super(statusCode);
    }

    public Unauthorized(String message, int statusCode) {
        super(message, statusCode);
    }

    public Unauthorized(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public Unauthorized(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
