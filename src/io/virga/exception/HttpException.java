/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

import java.io.IOException;

/**
 * Exception representing a HTTP error response code. See {@link io.virga.exception the package overview} for usage
 * details.
 */
@SuppressWarnings("UnusedDeclaration")
public class HttpException extends IOException {
    private static final long serialVersionUID = 1L;
    private int statusCode = 500;
    private String detail;

    public HttpException(final Throwable t) {
        super(t);
    }

    public HttpException(final String message) {
        super(message);
    }

    public HttpException(final String message, final Throwable t) {
        super(message, t);
    }

    public HttpException(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpException(String message, Throwable t, int statusCode) {
        super(message, t);
        this.statusCode = statusCode;
    }

    public HttpException(Throwable t, int statusCode) {
        super(t);
        this.statusCode = statusCode;
    }

    /**
     * Get the HTTP status code of the error response.
     *
     * @return the HTTP status code of the error response.
     */
    public int getStatusCode() {
        return (statusCode == -1) ? 500 : statusCode;
    }

    /**
     * Set the HTTP status code of the error response.
     *
     * @param statusCode the HTTP status code of the error response.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Get additional error detail retrieved from the HTTP error response body.
     *
     * @return the additional error detail, or null if there is no additional detail.
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Set additional error detail retrieved from the HTTP error response body.
     *
     * @param detail the additional error detail, or null if there is no additional detail.
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String toString() {
        return String.format("[%s:code=%d,message=%s]", this.getClass().getSimpleName(), statusCode, getMessage());
    }

    /**
     * Convenience method to instantiate the appropriate subclass of <code>HttpException</code>.
     *
     * @param statusCode the statusCode to create an exception for
     * @param cause the cause of the exception
     * @param message the error message to create an exception for
     * @return a new <code>HttpException</code> of the appropriate subclass.
     */
    public static HttpException fromCode(int statusCode, Throwable cause, String message) {
        switch (statusCode) {
            case 400:
                return new BadRequest(message, cause);
            case 401:
                return new Unauthorized(message, cause);
            case 403:
                return new Forbidden(message, cause);
            case 404:
                return new NotFound(message, cause);
            case 405:
                return new MethodNotAllowed(message, cause);
            case 406:
                return new NotAcceptable(message, cause);
            case 409:
                return new Conflict(message, cause);
            case 413:
                return new RequestEntityTooLarge(message, cause);
            case 415:
                return new RequestURITooLong(message, cause);
            case 500:
                return new InternalServerError(message, cause);
            case 501:
                return new NotImplemented(message, cause);
            default:
                if (statusCode >= 400 && statusCode < 500) {
                    return new BadRequest(message, cause, statusCode);
                } else {
                    return new InternalServerError(message, cause, statusCode);
                }
        }
    }
    
    /**
     * Convenience method to instantiate the appropriate subclass of <code>HttpException</code>.
     *
     * @param statusCode the statusCode to create an exception for
     * @param message the error message to create an exception for
     * @return a new <code>HttpException</code> of the appropriate subclass.
     */
    public static HttpException fromCode(int statusCode, String message) {
        return fromCode(statusCode, null, message);
    }

    /**
     * Convenience method to instantiate the appropriate subclass of <code>HttpException</code>.
     *
     * @param statusCode the statusCode to create an exception for
     * @return a new <code>HttpException</code> of the appropriate subclass.
     */
    public static HttpException fromCode(int statusCode) {
        return fromCode(statusCode, null, null);
    }

    /**
     * Convenience method to instantiate the appropriate subclass of <code>HttpException</code>.
     *
     * @param statusCode the statusCode to create an exception for
     * @param cause the cause of the exception
     * @return a new <code>HttpException</code> of the appropriate subclass.
     */
    public static HttpException fromCode(int statusCode, Throwable cause) {
        return fromCode(statusCode, cause, null);
    }
}
