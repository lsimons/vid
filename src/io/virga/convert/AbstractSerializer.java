/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import io.virga.exception.NotAcceptable;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.Writer;

public abstract class AbstractSerializer<T> implements Serializer<T> {
    @Override
    public void serialize(T object, Writer writer, MimeType type) throws IOException {
        if (MimeTypes.isHtml(type)) {
            html(object, writer);
        } else if (MimeTypes.isJson(type)) {
            json(object, writer);
        } else if (MimeTypes.isXml(type)) {
            xml(object, writer);
        } else if (MimeTypes.isText(type)) {
            text(object, writer);
        } else {
            throw new NotAcceptable();
        }
    }

    protected abstract void html(T object, Writer writer) throws IOException;

    protected abstract void json(T object, Writer writer) throws IOException;

    protected abstract void xml(T object, Writer writer) throws IOException;

    protected abstract void text(T object, Writer writer) throws IOException;

    @Override
    public boolean supports(Object object) {
        throw new UnsupportedOperationException("todo implement AbstractSerializer.supports()");
    }
}
