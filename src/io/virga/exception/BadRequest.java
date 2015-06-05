/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class BadRequest extends HttpException {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Bad Request";
    public static final int STATUS_CODE = 400;

    public BadRequest() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public BadRequest(Throwable t) {
        super(t, STATUS_CODE);
    }

    public BadRequest(String message) {
        super(message, STATUS_CODE);
    }

    public BadRequest(String message, Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public BadRequest(int statusCode) {
        super(statusCode);
    }

    public BadRequest(String message, int statusCode) {
        super(message, statusCode);
    }

    public BadRequest(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public BadRequest(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
