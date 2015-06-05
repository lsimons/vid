/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.serializers;

import io.virga.convert.AbstractSerializer;
import io.virga.convert.ConverterFactory;
import io.virga.convert.Markdown;
import io.virga.exception.InternalServerError;
import io.virga.json.JsonGenerator;
import io.virga.model.Link;
import io.virga.model.LinkSection;
import io.virga.model.Page;
import io.virga.model.PreFormattedSection;
import io.virga.model.Section;
import io.virga.model.TextSection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.PrintWriter;

import static io.virga.convert.HTML.footer;
import static io.virga.convert.HTML.header;
import static io.virga.convert.HTML.linkSection;
import static io.virga.convert.HTML.preFormattedSection;
import static io.virga.convert.HTML.textSection;
import static io.virga.convert.Markdown.H1;
import static io.virga.convert.Markdown.section;
import static io.virga.convert.XML.getStreamWriter;
import static io.virga.convert.XML.vidNs;
import static io.virga.convert.XML.writeBegin;
import static io.virga.convert.XML.writeEnd;
import static io.virga.convert.XML.writeLeaf;

public class PageSerializer extends AbstractSerializer<Page> {
    public static void load() {
        // no-op called to ensure class-loading
    }

    static {
        ConverterFactory.register(new PageSerializer());
    }

    @Override
    protected void html(Page object, Writer writer) throws IOException {
        PrintWriter pw = new PrintWriter(writer);
        header(pw, object.getTitle());
        for (Section section : object.getSections()) {
            if (section instanceof PreFormattedSection) {
                preFormattedSection(pw, section.getTitle(), ((PreFormattedSection) section).getContent());
            } else if (section instanceof TextSection) {
                textSection(pw, section.getTitle(), ((TextSection) section).getContent());
            } else if (section instanceof LinkSection) {
                linkSection(pw, section.getTitle(), ((LinkSection) section).getLinks());
            } else {
                throw new InternalServerError("Unrecognized section class " + section.getClass().getSimpleName());
            }
        }
        footer(pw);
        pw.flush();
    }

    @Override
    protected void json(Page object, Writer writer) throws IOException {
        JsonGenerator json = new JsonGenerator(writer)
                .prettyPrint()
                .startObject()
                .member("title", object.getTitle())
                .arrayMember("sections");
        for (Section section : object.getSections()) {
            json.startObject()
                    .member("title", section.getTitle());
            if (section instanceof PreFormattedSection) {
                json.member("content", ((PreFormattedSection) section).getContent());
            } else if (section instanceof TextSection) {
                json.member("content", ((TextSection) section).getContent());
            } else if (section instanceof LinkSection) {
                json.arrayMember("links");
                for (Link link : ((LinkSection) section).getLinks()) {
                    json.startObject()
                            .member("href", link.getTarget())
                            .member("text", link.getText())
                            .endObject();
                }
                json.endArray();
            }
            json.endObject();
        }
        json.endArray()
                .endObject()
                .flush();
    }

    @Override
    protected void xml(Page object, Writer writer) throws IOException {
        try {
            XMLStreamWriter xml = getStreamWriter(writer);
            writeBegin(xml, vidNs, "page", "vid");
            writeLeaf(xml, vidNs, "title", object.getTitle());
            xml.writeCharacters("\n");
            for (Section section : object.getSections()) {
                xml.writeStartElement(vidNs, "section");
                xml.writeCharacters("\n");
                writeLeaf(xml, vidNs, "title", object.getTitle());
                xml.writeCharacters("\n");
                if (section instanceof PreFormattedSection) {
                    xml.writeStartElement(vidNs, "pre");
                    xml.writeCData(((PreFormattedSection) section).getContent());
                    xml.writeEndElement();
                    xml.writeCharacters("\n");
                } else if (section instanceof TextSection) {
                    xml.writeStartElement(vidNs, "p");
                    xml.writeCData(((TextSection) section).getContent());
                    xml.writeEndElement();
                    xml.writeCharacters("\n");
                } else if (section instanceof LinkSection) {
                    xml.writeStartElement(vidNs, "links");
                    xml.writeCharacters("\n");
                    for (Link link : ((LinkSection) section).getLinks()) {
                        xml.writeStartElement(vidNs, "a");
                        xml.writeAttribute(vidNs, "href", link.getTarget());
                        xml.writeCharacters(link.getText());
                        xml.writeEndElement();
                        xml.writeCharacters("\n");
                    }
                    xml.writeEndElement();
                    xml.writeCharacters("\n");
                }
                xml.writeEndElement();
                xml.writeCharacters("\n");
            }
            writeEnd(xml);
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void text(Page object, Writer writer) throws IOException {
        PrintWriter pw = new PrintWriter(writer);
        H1(pw, object.getTitle());
        pw.println();
        for (Section section : object.getSections()) {
            if (section instanceof PreFormattedSection) {
                section(pw, section.getTitle(), ((PreFormattedSection) section).getContent());
            } else if (section instanceof TextSection) {
                section(pw, section.getTitle(), ((TextSection) section).getContent());
            } else if (section instanceof LinkSection) {
                Markdown.linkSection(pw, section.getTitle(), ((LinkSection) section).getLinks());
            }
        }
        pw.flush();
    }

    @Override
    public boolean supports(Object object) {
        return object instanceof Page;
    }
}
