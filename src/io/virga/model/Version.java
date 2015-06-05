/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.model;

import static io.virga.Config.VERSION;

public class Version {
    public static Version getSystemVersion() {
        return new Version(VERSION);
    }

    private String value;

    public Version(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
