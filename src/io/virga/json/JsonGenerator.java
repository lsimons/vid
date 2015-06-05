/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.json;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import static io.virga.json.JsonGeneratorState.IN_ARRAY;
import static io.virga.json.JsonGeneratorState.IN_OBJECT;

public class JsonGenerator {
    private final static char COLON = ':';
    private final static char COMMA = ',';
    private final static char END_ARRAY = ']';
    private final static char END_OBJECT = '}';
    private final static char ESCAPE = '\\';
    private final static char LF = '\n';
    private final static char QUOTE = '"';
    private final static char SP = ' ';
    private final static char START_ARRAY = '[';
    private final static char START_OBJECT = '{';

    private final static char[] EMPTY = new char[]{QUOTE, QUOTE};
    private final static char[] INDENT_CHARS = new char[]{SP, SP, SP, SP};
    private final static char[] NULL = new char[]{'n', 'u', 'l', 'l'};

    private boolean begin = true;
    private boolean needComma = false;
    private boolean prettyPrint = false;
    private List<JsonGeneratorState> stack = new LinkedList<>();
    private boolean suppressNewline = false;
    private Writer writer;

    public JsonGenerator(Writer writer) {
        if (writer == null) {
            throw new NullPointerException("writer cannot be null");
        }
        this.writer = writer;
    }

    public JsonGenerator endArray() throws IOException {
        pop();
        indent();
        writer.append(END_ARRAY);
        writer.flush();
        return this;
    }

    public JsonGenerator endObject() throws IOException {
        pop();
        indent();
        writer.append(END_OBJECT);
        writer.flush();
        return this;
    }

    public JsonGenerator member(String name, String value) throws IOException {
        return member(name, value, true);
    }

    public JsonGenerator member(String name, String value, boolean quote) throws IOException {
        name(name);
        value(value, quote);
        return this;
    }

    public JsonGenerator member(String name, int value) throws IOException {
        return member(name, Integer.toString(value), false);
    }

    public JsonGenerator member(String name, long value) throws IOException {
        return member(name, Long.toString(value), false);
    }

    public JsonGenerator member(String name, float value) throws IOException {
        return member(name, Float.toString(value), false);
    }

    public JsonGenerator member(String name, double value) throws IOException {
        return member(name, Double.toString(value), false);
    }

    public JsonGenerator member(String name, boolean value) throws IOException {
        return member(name, Boolean.toString(value), false);
    }

    public JsonGenerator name(String name) throws IOException {
        comma();
        needComma = false;
        if (name == null) {
            return this;
        }
        indent();
        suppressNewline = true;
        if (name.isEmpty()) {
            writer.write(EMPTY);
        }
        writer.append(QUOTE);
        writer.append(escape(name));
        writer.append(QUOTE);
        writer.append(COLON);
        return this;
    }

    public JsonGenerator nullMember(String name) throws IOException {
        return member(name, null, false);
    }
    
    public JsonGenerator arrayMember(String name) throws IOException {
        name(name);
        return startArray();
    }

    public JsonGenerator nullValue() throws IOException {
        return value(null, false);
    }

    public JsonGenerator optionalMember(String name, String value) throws IOException {
        if (value != null) {
            member(name, value);
        }
        return this;
    }

    public JsonGenerator prettyPrint() {
        this.prettyPrint = true;
        return this;
    }

    public JsonGenerator startArray() throws IOException {
        comma();
        indent();
        writer.append(START_ARRAY);
        stack.add(IN_ARRAY);
        needComma = false;
        return this;
    }

    public JsonGenerator startObject() throws IOException {
        comma();
        indent();
        writer.append(START_OBJECT);
        stack.add(IN_OBJECT);
        needComma = false;
        return this;
    }

    public JsonGenerator value(String value) throws IOException {
        return value(value, true);
    }

    public JsonGenerator value(int value) throws IOException {
        return value(Integer.toString(value), false);
    }

    public JsonGenerator value(long value) throws IOException {
        return value(Long.toString(value), false);
    }

    public JsonGenerator value(float value) throws IOException {
        return value(Float.toString(value), false);
    }

    public JsonGenerator value(double value) throws IOException {
        return value(Double.toString(value), false);
    }

    public JsonGenerator value(boolean value) throws IOException {
        return value(Boolean.toString(value), false);
    }
    
    public JsonGenerator flush() throws IOException {
        if (stack.size() == 0) {
            writer.append('\n');
        }
        writer.flush();
        return this;
    }

    private void comma() throws IOException {
        if (needComma) {
            writer.append(COMMA);
        }
        needComma = true;
    }

    private String escape(String str) {
        StringBuilder result = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c == QUOTE) {
                result.append(ESCAPE);
                result.append(QUOTE);
            } else if (c == ESCAPE) {
                result.append(ESCAPE);
                result.append(ESCAPE);
            } else if (Character.isISOControl(c)) {
                result.append(ESCAPE);
                result.append('u');
                String encoded = Integer.toString(c, 16);
                while (encoded.length() < 4) {
                    encoded = "0" + encoded;
                }
                result.append(encoded);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private void indent() throws IOException {
        if (!prettyPrint) {
            return;
        }
        if (suppressNewline) {
            suppressNewline = false;
            writer.append(SP);
        } else {
            if (begin) {
                begin = false;
            } else {
                writer.append(LF);
                for (int i = 0; i < stack.size(); i++) {
                    writer.write(INDENT_CHARS);
                }
            }
        }
    }

    private void pop() {
        if (stack.size() > 0) {
            stack.remove(stack.size() - 1);
        }
        needComma = true;
    }

    private JsonGenerator value(String value, boolean quote) throws IOException {
        comma();
        indent();
        if (value == null) {
            writer.write(NULL);
        } else {
            if (quote) {
                writer.append(QUOTE);
                writer.append(escape(value));
                writer.append(QUOTE);
            } else {
                writer.append(value);
            }
        }
        return this;
    }
}
