/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class IO {
    public static PrintWriter utf8Writer(OutputStream os) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        return pw;
    }
}
