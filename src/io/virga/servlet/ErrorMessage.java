/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.exception.HttpException;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorMessage {
    private int code;
    private String message;
    private String detail;
    private String stackTrace;

    public ErrorMessage(Throwable t, boolean showStackTrace) {
        setMessage(t.getMessage());
        if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            setCode(httpException.getStatusCode());
            setDetail(httpException.getDetail());
        } else {
            setCode(500);
        }
        if (showStackTrace) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            setStackTrace(sw.toString());
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
