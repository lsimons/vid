/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.serializers;

import io.virga.convert.AbstractSerializer;
import io.virga.convert.ConverterFactory;
import io.virga.json.JsonGenerator;
import io.virga.model.Version;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.PrintWriter;

import static io.virga.convert.HTML.footer;
import static io.virga.convert.HTML.header;
import static io.virga.convert.XML.getStreamWriter;
import static io.virga.convert.XML.vidNs;
import static io.virga.convert.XML.writeBegin;
import static io.virga.convert.XML.writeEnd;

public class VersionSerializer extends AbstractSerializer<Version> {
    public static void load() {
        // no-op called to ensure class-loading
    }

    static {
        ConverterFactory.register(new VersionSerializer());
    }

    @Override
    protected void html(Version object, Writer writer) throws IOException {
        PrintWriter pw = new PrintWriter(writer);
        header(pw, "Version");
        pw.println("<div id=\"version\">" + object.getValue() + "</div>");
        pw.println("<p><a href=\"javascript:history.go(-1)\">back</a></p>");
        footer(pw);
        pw.flush();
    }

    @Override
    protected void json(Version object, Writer writer) throws IOException {
        new JsonGenerator(writer)
                .prettyPrint()
                .startObject()
                .member("version", object.getValue())
                .endObject()
                .flush();
    }

    @Override
    protected void xml(Version object, Writer writer) throws IOException {
        try {
            XMLStreamWriter xml = getStreamWriter(writer);
            writeBegin(xml, vidNs, "version", "vid");
            xml.writeCharacters(object.getValue());
            writeEnd(xml);
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void text(Version object, Writer writer) throws IOException {
        PrintWriter pw = new PrintWriter(writer);
        pw.println(object.getValue());
        pw.flush();
    }

    @Override
    public boolean supports(Object object) {
        return object instanceof Version;
    }
}
