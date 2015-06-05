/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.json;

import org.testng.annotations.Test;

//import java.io.PrintWriter;
import java.io.StringWriter;

@Test(groups = "functional")
public class JsonGeneratorTest {
    private JsonGenerator generator;
    
    public void testBasics() throws Exception {
        //PrintWriter pw = new PrintWriter(System.out);
        StringWriter sw = new StringWriter();
        new JsonGenerator(sw)
                .prettyPrint()
                .startObject()
                .member("foo", "bar")
                .name("cheese")
                .startArray()
                .value("cake")
                .value("is")
                .value("a")
                .value("lie")
                .endArray()
                .member("quoting", "\\o/ this is a \"good\" idea")
                .member("control chars", "I'm not afraid of \u0000")
                .member("integer", 0)
                .member("long", 0L)
                .member("float", 0f)
                .member("double", 0d)
                .member("boolean", false)
                .nullMember("nullMember")
                .member("nullString", null)
                .name("primitiveArray")
                .startArray()
                .value(0)
                .value(0L)
                .value(0f)
                .value(0d)
                .value(true)
                .nullValue()
                .value(null)
                .endArray()
                .endObject();
    }
}
