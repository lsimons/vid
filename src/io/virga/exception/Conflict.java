/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class Conflict extends BadRequest {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Conflict";
    public static final int STATUS_CODE = 409;

    public Conflict() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public Conflict(Throwable t) {
        super(DEFAULT_MESSAGE, t, STATUS_CODE);
    }

    public Conflict(String message) {
        super(message, STATUS_CODE);
    }

    public Conflict(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public Conflict(int statusCode) {
        super(statusCode);
    }

    public Conflict(String message, int statusCode) {
        super(message, statusCode);
    }

    public Conflict(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public Conflict(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
