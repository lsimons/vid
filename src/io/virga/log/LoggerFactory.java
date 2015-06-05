/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public class LoggerFactory {
    private static Method delegate;

    static {
        try {
            Class delegateClass = Class.forName("org.slf4j.LoggerFactory");
            delegate = delegateClass.getMethod("getLogger", Class.class);
        } catch (ClassNotFoundException e) {
            delegate = null;
        } catch (NoSuchMethodException e) {
            delegate = null;
        }
    }

    private static Constructor<?> wrapper;

    static {
        try {
            Class wrapperClass = Class.forName("io.virga.log.SLF4JLogger");
            wrapper = wrapperClass.getConstructor(Object.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            wrapper = null;
        }
    }

    public static Logger getLogger(Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        if (delegate == null || wrapper == null) {
            return new ConsoleLogger(clazz.getCanonicalName());
        } else {
            try {
                return wrap(clazz, delegate.invoke(null, clazz));
            } catch (IllegalAccessException | InvocationTargetException e) {
                return new ConsoleLogger(clazz.getCanonicalName());
            }
        }
    }

    private static Logger wrap(Class clazz, Object target) {
        try {
            return (Logger) wrapper.newInstance(target);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return new ConsoleLogger(clazz.getCanonicalName());
        }
    }
}
