/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.json;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

public class JsonParserTest {
    String empty = "{}";
    String simple = "{\"a\": true}";
    String simpleArray = "[\"a\", true]";
    String basicValues = "{\"key\": \"b\", \"key2\": true, \"d\": false, \"e\": null, \"f\": 12.4}";
    String nested = "{\"key\": \"b\", \"key2\": [true, false, \"e\", 12.5]}";
    String trailingComma = "{\"foo\": \"bar\",}";
    
    JsonParser parser;
    
    @DataProvider(name = "testNames")
    public String[][] getTestData() {
        return new String[][] {
                { empty },
                { simple },
                { simpleArray },
                { basicValues },
                { nested },
                { trailingComma }
        };
    }
    
    @Test(groups = {"functional"}, dataProvider = "testNames")
    public void testBasics(String testString) throws Exception {
        parser = new JsonParser(testString);
        JsonToken token = null;
        while (token != JsonToken.END)
        {
            token = parser.next();
            switch (token) {
                case CONTINUE:
                    break;
                case NAME:
                    /*String name =*/ parser.getString();
                    break;
                case VALUE:
                    /*String value =*/ parser.getString();
                    parser.getBoolean();
                    try {
                        parser.getInteger();
                        parser.getFloat();
                        parser.getDouble();
                    } catch (NumberFormatException e) {
                        //System.out.println("NumberFormatException " + e.getMessage());
                    }
                    break;
                default:
                    //System.out.println(token);
            }
        }
        System.out.flush();
    }
    
    @Test(groups = {"functional"})
    public void testInputStream() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(basicValues.getBytes(Charset.forName("UTF-8")));
        parser = new JsonParser(is);
        JsonToken token = null;
        while (token != JsonToken.END) {
            token = parser.next();
        }
    }
}
