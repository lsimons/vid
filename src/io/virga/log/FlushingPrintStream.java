/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.log;

import java.io.PrintStream;

public class FlushingPrintStream extends PrintStream {
    public FlushingPrintStream(PrintStream out) {
        super(out);
    }

    @Override
    public void println() {
        super.println();
        super.flush();
    }

    @Override
    public void println(boolean b) {
        super.println(b);
        super.flush();
    }

    @Override
    public void println(char c) {
        super.println(c);
        super.flush();
    }

    @Override
    public void println(int i) {
        super.println(i);
        super.flush();
    }

    @Override
    public void println(long l) {
        super.println(l);
        super.flush();
    }

    @Override
    public void println(float v) {
        super.println(v);
        super.flush();
    }

    @Override
    public void println(double v) {
        super.println(v);
        super.flush();
    }

    @Override
    public void println(char[] chars) {
        super.println(chars);
        super.flush();
    }

    @Override
    public void println(String s) {
        super.println(s);
        super.flush();
    }

    @Override
    public void println(Object o) {
        super.println(o);
        super.flush();
    }
}
