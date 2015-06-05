/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class Page {
    private String title;
    private List<Section> sections = new ArrayList<>();

    public Page(String title, Section... sections) {
        this.title = title;
        Collections.addAll(this.sections, sections);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Section[] getSections() {
        return sections.toArray(new Section[sections.size()]);
    }
}
