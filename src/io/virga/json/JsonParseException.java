/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.json;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class JsonParseException extends IOException {
    public JsonParseException() {
    }

    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonParseException(Throwable cause) {
        super(cause);
    }
}
