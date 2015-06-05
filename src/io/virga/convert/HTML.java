/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import io.virga.model.Link;
import io.virga.model.Version;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static io.virga.Config.DEBUG;

public class HTML {
    public final static List<String> allJavascript = Arrays.asList("/js/xhr.js");

    public static void footer(PrintWriter pw) {
        pw.println("</body></html>");
    }

    public static void header(PrintWriter pw, String title, Object... titleParameters) {
        pw.println("<html><head>");
        pw.println("<!-- version " + Version.getSystemVersion().getValue() + " -->");
        if (title != null) {
            if (titleParameters.length > 0) {
                title = String.format(title, titleParameters);
            }
            pw.println("<title>" + title + "</title>");
        }
        if (DEBUG) {
            //printScript(pw, "https://getfirebug.com/firebug-lite.js#startOpened");
            for (String javascript : allJavascript) {
                printScript(pw, javascript);
            }
        } else {
            printScript(pw, "/min.js");
        }
        pw.println("</head><body>");
        if (title != null) {
            pw.println("<header><h1>" + title + "</h1></header>");
        }
    }

    public static void linkSection(PrintWriter pw, String header, Link... links) {
        if (links.length == 0) {
            return;
        }
        pw.println("<section>");
        pw.println("<h2>" + header + "</h2>");
        pw.println("<ul>");
        for (Link link : links) {
            pw.println(String.format("  <li><a href=\"%s\">%s</a></li>", unsafeAttributeEscape(link.getTarget()),
                    unsafeEscape(link.getText())));
        }
        pw.println("</ul>");
        pw.println("</section>");
    }

    public static void preFormattedSection(PrintWriter pw, String header, String content) {
        if (content == null) {
            return;
        }
        // this is not intended as XSS prevention (the error message content should
        // be sanitized on/before creation), only as a basic programmer safety for
        // strings generated in the software itself
        content = unsafeEscape(content);
        pw.println("<section>");
        pw.println("<h2>" + header + "</h2>");
        pw.println("<pre>");
        pw.println(content);
        pw.println("</pre>");
        pw.println("</section>");
    }

    public static void textSection(PrintWriter pw, String header, String content) {
        if (content == null) {
            return;
        }
        // this is not intended as XSS prevention (the error message content should
        // be sanitized on/before creation), only as a basic programmer safety for
        // strings generated in the software itself
        content = unsafeEscape(content);
        pw.println("<section>");
        pw.println("<h2>" + header + "</h2>");
        pw.println("<p>");
        pw.println(content);
        pw.println("</p>");
        pw.println("</section>");
    }

    public static void printScript(PrintWriter pw, String javascript) {
        pw.println("<script type=\"text/javascript\" src=\"" + javascript + "\"></script>");
    }

    /**
     * A basic 'try to make this string printable in HTML attributes' operation that can only be used on trusted
     * strings; i.e. it <strong>cannot</strong> be used to sanitize HTML.
     *
     * @param str the string to modify
     * @return the modified string
     */
    public static String unsafeAttributeEscape(String str) {
        str = str.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&", "&amp;").replaceAll("\"", "\\\"");
        return str;
    }

    /**
     * A basic 'try to make this string printable in HTML' operation that can only be used on trusted strings; i.e. it
     * <strong>cannot</strong> be used to sanitize HTML.
     *
     * @param str the string to modify
     * @return the modified string
     */
    public static String unsafeEscape(String str) {
        str = str.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&", "&amp;");
        return str;
    }
}
