/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.json;

public enum JsonParserState {
    BEGIN,
    IN_OBJECT,
    IN_ARRAY,
    NAME,
    NAME_ESCAPE,
    NAME_UNICODE_ESCAPE,
    NAME_END,
    VALUE_START,
    VALUE_STRING,
    VALUE_STRING_ESCAPE,
    VALUE_STRING_ESCAPE_UNICODE,
    VALUE_NUMBER,
    VALUE_TRUE_T,
    VALUE_TRUE_R,
    VALUE_TRUE_U,
    VALUE_FALSE_F,
    VALUE_FALSE_A,
    VALUE_FALSE_L,
    VALUE_FALSE_S,
    VALUE_NULL_N,
    VALUE_NULL_U,
    VALUE_NULL_L,
    NEXT_VALUE,
    END;
}
