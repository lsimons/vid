/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.model;

@SuppressWarnings("UnusedDeclaration")
public class PreFormattedSection extends Section {
    String content;

    public PreFormattedSection(String title, String content) {
        super(title);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
