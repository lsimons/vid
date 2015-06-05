/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.log;

@SuppressWarnings("UnusedDeclaration")
public interface Logger {
    public boolean isTraceEnabled();

    public void trace(Object... args);

    public void trace(Object message, Throwable throwable);

    public boolean isDebugEnabled();

    public void debug(Object... args);

    public void debug(Object message, Throwable throwable);

    public boolean isInfoEnabled();

    public void info(Object... args);

    public void info(Object message, Throwable throwable);

    public boolean isWarnEnabled();

    public void warn(Object... args);

    public void warn(Object message, Throwable throwable);

    public boolean isErrorEnabled();

    public void error(Object... args);

    public void error(Object message, Throwable throwable);
}
