/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class MethodNotAllowed extends BadRequest {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Method Not Allowed";
    public static final int STATUS_CODE = 405;

    public MethodNotAllowed() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public MethodNotAllowed(Throwable t) {
        super(t, STATUS_CODE);
    }

    public MethodNotAllowed(String message) {
        super(message, STATUS_CODE);
    }

    public MethodNotAllowed(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public MethodNotAllowed(int statusCode) {
        super(statusCode);
    }

    public MethodNotAllowed(String message, int statusCode) {
        super(message, statusCode);
    }

    public MethodNotAllowed(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public MethodNotAllowed(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
