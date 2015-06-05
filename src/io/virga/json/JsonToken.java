/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.json;

public enum JsonToken {
    START_OBJECT,
    END_OBJECT,
    START_ARRAY,
    END_ARRAY,
    NAME,
    VALUE,
    CONTINUE,
    END;
}
