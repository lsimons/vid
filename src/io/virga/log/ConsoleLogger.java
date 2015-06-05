/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.log;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static io.virga.Config.DEBUG;
import static io.virga.Config.TRACE;

public class ConsoleLogger implements Logger {
    private final PrintStream target;
    private final String name;
    private final DateFormat dateFormat;

    public ConsoleLogger(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.target = new FlushingPrintStream(System.err);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.name = name;
    }

    @Override
    public boolean isTraceEnabled() {
        return TRACE;
    }

    @Override
    public void trace(Object... args) {
        log("TRACE", args);
    }

    @Override
    public void trace(Object message, Throwable throwable) {
        log("TRACE", throwable, message);
    }

    @Override
    public boolean isDebugEnabled() {
        return DEBUG;
    }

    @Override
    public void debug(Object... args) {
        log("DEBUG", args);
    }

    @Override
    public void debug(Object message, Throwable throwable) {
        log("DEBUG", throwable, message);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(Object... args) {
        log("INFO", args);
    }

    @Override
    public void info(Object message, Throwable throwable) {
        log("INFO", throwable, message);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(Object... args) {
        log("WARN", args);
    }

    @Override
    public void warn(Object message, Throwable throwable) {
        log("WARN", throwable, message);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(Object... args) {
        log("ERROR", args);
    }

    @Override
    public void error(Object message, Throwable throwable) {
        log("ERROR", throwable, message);
    }

    private void log(String level, Object... args) {
        log(level, null, args);
    }

    private void log(String level, Throwable throwable, Object... args) {
        try {
            target.print(dateFormat.format(new Date()));
            target.print(":");
            target.print(level);
            target.print(":");
            target.print(name);
            target.print(": ");

            if (args.length == 0) {
                target.println("No message");
            } else if (args.length == 1) {
                target.println(args[0]);
            } else if (args[0] != null) {
                String message = args[0].toString();
                Object[] messageArgs = Arrays.copyOfRange(args, 1, args.length);
                String formatted = String.format(message, messageArgs);
                target.println(formatted);
            } else {
                for (Object arg : args) {
                    target.print(arg);
                    target.print(" ");
                }
                target.println();
            }

            if (throwable == null && args.length > 0) {
                for (Object arg : args) {
                    if (arg instanceof Throwable) {
                        throwable = (Throwable) arg;
                        break;
                    }
                }
            }

            if (throwable != null) {
                throwable.printStackTrace(target);
            }
        } catch (Exception e) {
            target.println("Error while logging: " + e.getMessage());
            e.printStackTrace(target);
        }
    }
}
