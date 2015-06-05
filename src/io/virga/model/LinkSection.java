/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkSection extends Section {
    private List<Link> links = new ArrayList<>();

    public LinkSection(String title, Link... links) {
        super(title);
        Collections.addAll(this.links, links);
    }

    public Link[] getLinks() {
        return links.toArray(new Link[links.size()]);
    }
}
