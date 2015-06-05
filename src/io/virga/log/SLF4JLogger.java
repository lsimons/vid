/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.log;

import java.util.Arrays;

@SuppressWarnings("UnusedDeclaration")
public class SLF4JLogger implements Logger {
    private org.slf4j.Logger delegate;

    public SLF4JLogger(Object logger) {
        delegate = (org.slf4j.Logger) logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public void trace(Object... args) {
        String message = message(args);
        Object[] rest = rest(args);
        delegate.trace(message, rest);
    }

    @Override
    public void trace(Object message, Throwable throwable) {
        delegate.trace(message == null ? "" : message.toString(), throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public void debug(Object... args) {
        String message = message(args);
        Object[] rest = rest(args);
        delegate.debug(message, rest);
    }

    @Override
    public void debug(Object message, Throwable throwable) {
        delegate.debug(message == null ? "" : message.toString(), throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(Object... args) {
        String message = message(args);
        Object[] rest = rest(args);
        delegate.info(message, rest);
    }

    @Override
    public void info(Object message, Throwable throwable) {
        delegate.info(message == null ? "" : message.toString(), throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(Object... args) {
        String message = message(args);
        Object[] rest = rest(args);
        delegate.warn(message, rest);
    }

    @Override
    public void warn(Object message, Throwable throwable) {
        delegate.warn(message == null ? "" : message.toString(), throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(Object... args) {
        String message = message(args);
        Object[] rest = rest(args);
        delegate.error(message, rest);
    }

    @Override
    public void error(Object message, Throwable throwable) {
        delegate.error(message == null ? "" : message.toString(), throwable);
    }

    private Object[] rest(Object[] args) {
        return args.length <= 1 ? new Object[0] : Arrays.copyOfRange(args, 1, args.length);
    }

    private String message(Object[] args) {
        return args.length == 0 ? "" : args[0] == null ? "" : args[0].toString();
    }
}
