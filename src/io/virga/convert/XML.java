/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Writer;

public class XML {
    public final static String vidNs = "http://id.virga.io/ns/vid/1.0";
    private final static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private final static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

    public static XMLInputFactory getInputFactory() {
        return xmlInputFactory;
    }

    public static XMLStreamWriter getStreamWriter(OutputStream os) throws XMLStreamException {
        return getOutputFactory().createXMLStreamWriter(os, "UTF-8");
    }

    public static XMLStreamWriter getStreamWriter(Writer writer) throws XMLStreamException {
        return getOutputFactory().createXMLStreamWriter(writer);
    }

    public static XMLOutputFactory getOutputFactory() {
        return xmlOutputFactory;
    }

    public static void writeBegin(XMLStreamWriter writer, String ns, String localName, String prefix)
            throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters("\n");
        writer.setPrefix(prefix, ns);
        writer.setDefaultNamespace(ns);
        writer.writeStartElement(ns, localName);
        writer.writeNamespace(prefix, ns);
        writer.writeDefaultNamespace(ns);
        writer.writeCharacters("\n");
    }

    public static void writeEnd(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCharacters("\n");
        writer.writeEndElement();
        writer.writeCharacters("\n");
        writer.writeEndDocument();
        writer.flush();
    }

    public static void writeLeaf(XMLStreamWriter writer, String ns, String localName, int number)
            throws XMLStreamException {
        writeLeaf(writer, ns, localName, Integer.toString(number));
    }

    public static void writeOptionalLeaf(XMLStreamWriter writer, String ns, String localName, String characters)
            throws XMLStreamException {
        if (characters == null) {
            return;
        }
        writeLeaf(writer, ns, localName, characters);
    }

    public static void writeLeaf(XMLStreamWriter writer, String ns, String localName, String characters)
            throws XMLStreamException {
        writer.writeStartElement(ns, localName);
        writer.writeCharacters(characters);
        writer.writeEndElement();
    }
}
