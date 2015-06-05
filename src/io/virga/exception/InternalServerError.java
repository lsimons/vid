/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

@SuppressWarnings("UnusedDeclaration")
public class InternalServerError extends HttpException {
    private static final long serialVersionUID = 1L;
    public final static String DEFAULT_MESSAGE = "Internal Server Error";
    public static final int STATUS_CODE = 500;

    public InternalServerError() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public InternalServerError(final Throwable t) {
        super(t, STATUS_CODE);
    }

    public InternalServerError(final String message) {
        super(message, STATUS_CODE);
    }

    public InternalServerError(final String message, final Throwable t) {
        super(message, t, STATUS_CODE);
    }

    public InternalServerError(int statusCode) {
        super(statusCode);
    }

    public InternalServerError(String message, int statusCode) {
        super(message, statusCode);
    }

    public InternalServerError(String message, Throwable t, int statusCode) {
        super(message, t, statusCode);
    }

    public InternalServerError(Throwable t, int statusCode) {
        super(t, statusCode);
    }
}
