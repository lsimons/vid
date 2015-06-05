/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

public class SerializerNotFoundException extends InternalServerError {
    public <T> SerializerNotFoundException(T object) {
        super(String.format("No serializer found for objects of type %s", object == null ? null : object.getClass()));
    }
}
