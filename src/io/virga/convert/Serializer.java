/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.Writer;

public interface Serializer<T> {
    void serialize(T object, Writer writer, MimeType type) throws IOException;

    boolean supports(Object object);
}
