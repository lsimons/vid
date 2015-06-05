/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import io.virga.model.Link;

import java.io.PrintWriter;

public class Markdown {
    public final static void H1(PrintWriter pw, String header, Object... arguments) {
        header(pw, "=", header, arguments);
    }

    public final static void header(PrintWriter pw, String underline, String header, Object... arguments) {
        if (arguments.length > 0) {
            header = String.format(header, arguments);
        }
        pw.println(header);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < header.length(); i++) {
            buf.append(underline);
        }
        pw.println(buf);
    }

    public static void linkSection(PrintWriter pw, String header, Link... links) {
        if (links.length == 0) {
            return;
        }
        H2(pw, header);
        for (Link link : links) {
            pw.println(String.format("  * %s: %s", link.getText(), link.getTarget()));
        }
        pw.println();
    }

    public static void section(PrintWriter pw, String header, String section, Object... arguments) {
        if (section != null) {
            H2(pw, header, arguments);
            pw.println(section);
            pw.println();
        }
    }

    public final static void H2(PrintWriter pw, String header, Object... arguments) {
        header(pw, "-", header, arguments);
    }
}
