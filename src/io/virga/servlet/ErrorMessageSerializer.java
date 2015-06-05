/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.servlet;

import io.virga.convert.AbstractSerializer;
import io.virga.convert.ConverterFactory;
import io.virga.json.JsonGenerator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.PrintWriter;

import static io.virga.convert.HTML.footer;
import static io.virga.convert.HTML.header;
import static io.virga.convert.HTML.preFormattedSection;
import static io.virga.convert.Markdown.H1;
import static io.virga.convert.Markdown.section;
import static io.virga.convert.XML.getStreamWriter;
import static io.virga.convert.XML.vidNs;
import static io.virga.convert.XML.writeBegin;
import static io.virga.convert.XML.writeEnd;
import static io.virga.convert.XML.writeLeaf;
import static io.virga.convert.XML.writeOptionalLeaf;

public class ErrorMessageSerializer extends AbstractSerializer<ErrorMessage> {
    public static void load() {
        // no-op called to ensure class-loading
    }

    static {
        ConverterFactory.register(new ErrorMessageSerializer());
    }

    @Override
    protected void html(ErrorMessage object, Writer writer) throws IOException {
        PrintWriter pw = new PrintWriter(writer);
        header(pw, "Error %s: %s", object.getCode(), object.getMessage());
        preFormattedSection(pw, "Details", object.getDetail());
        preFormattedSection(pw, "Stack trace", object.getStackTrace());
        footer(pw);
        pw.flush();
    }

    @Override
    protected void json(ErrorMessage object, Writer writer) throws IOException {
        new JsonGenerator(writer)
                .prettyPrint()
                .startObject()
                .member("code", object.getCode())
                .optionalMember("message", object.getMessage())
                .optionalMember("detail", object.getDetail())
                .optionalMember("stackTrace", object.getStackTrace())
                .endObject()
                .flush();
    }

    @Override
    protected void xml(ErrorMessage object, Writer writer) throws IOException {
        try {
            XMLStreamWriter xml = getStreamWriter(writer);
            writeBegin(xml, vidNs, "error", "vid");
            writeLeaf(xml, vidNs, "code", object.getCode());
            writeOptionalLeaf(xml, vidNs, "message", object.getMessage());
            writeOptionalLeaf(xml, vidNs, "detail", object.getDetail());
            writeOptionalLeaf(xml, vidNs, "stackTrace", object.getStackTrace());
            writeEnd(xml);
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void text(ErrorMessage object, Writer writer) throws IOException {
        PrintWriter pw = new PrintWriter(writer);
        H1(pw, "Error %s: %s", object.getCode(), object.getMessage());
        pw.println();
        section(pw, "Details", object.getDetail());
        section(pw, "Stack trace", object.getStackTrace());
        pw.flush();
    }

    @Override
    public boolean supports(Object object) {
        return object instanceof ErrorMessage;
    }
}
