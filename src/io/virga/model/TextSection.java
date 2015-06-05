/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.model;

@SuppressWarnings("UnusedDeclaration")
public class TextSection extends Section {
    String content;

    public TextSection(String title, String content) {
        super(title);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
