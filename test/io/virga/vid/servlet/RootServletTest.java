/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.vid.servlet;

import io.virga.json.JsonParser;
import io.virga.json.JsonToken;
import org.testng.annotations.Test;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = {"functional"})
public class RootServletTest extends AbstractObjectServletTest {
    @Test
    public void testGetHtml() throws Exception {
        getResponse();
        assertObjectResponse();
        assertHtml();
        assertTrue(response.getLinks().length > 0);
        assertTrue(response.getElementsByTagName("script").length > 0);
        assertTrue(response.getElementsByTagName("title").length > 0);
        assertTrue(response.getElementsByTagName("body").length > 0);

        newRequest();
        acceptHtml();
        getResponse();
        assertHtml();
        assertTrue(response.getLinks().length > 0);
        assertTrue(response.getElementsByTagName("script").length > 0);
        assertTrue(response.getElementsByTagName("title").length > 0);
        assertTrue(response.getElementsByTagName("body").length > 0);
    }

    @Test(groups = {"checkin"})
    public void testGetJson() throws Exception {
        acceptJson();
        getResponse();
        assertObjectResponse();
        assertJson();
        JsonParser parser = getJsonParser();
        JsonToken token;
        int foundHref = 0;
        int foundText = 0;
        String fieldName = null;
        String fieldValue;
        while ((token = parser.next()) != JsonToken.END) {
            switch (token) {
                case NAME:
                    fieldName = parser.getString();
                    break;
                case VALUE:
                    fieldValue = parser.getString();
                    if ("href".equals(fieldName)) {
                        foundHref++;
                    } else if ("text".equals(fieldName)) {
                        foundText++;
                    } else {
                        continue;
                    }
                    assertNotNull(fieldValue);
                    break;
                default:
                    continue;
            }
        }
        assertTrue(foundHref > 0);
        assertTrue(foundText > 0);
    }

    @Test
    public void testGetText() throws Exception {
        acceptText();
        getResponse();
        assertObjectResponse();
        assertText();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testGetXml() throws Exception {
        acceptXml();
        getResponse();
        assertObjectResponse();
        assertXml();
        XMLStreamReader reader = getXmlStreamReader();
        int foundLink = 0;
        boolean readingLink = false;
        boolean linkHasText = false;
        while (reader.hasNext()) {
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event &&
                    reader.hasName() && "a".equals(reader.getLocalName())) {
                if (readingLink) {
                    throw new RuntimeException("nested <a/>");
                }

                foundLink++;
                int attributes = reader.getAttributeCount();
                for (int i = 0; i < attributes; i++) {
                    String attributeLocalName = reader.getAttributeLocalName(i);
                    if ("href".equals(attributeLocalName)) {
                        String fieldValue = reader.getAttributeValue(i);
                        assertNotNull(fieldValue);
                    }
                }
                readingLink = true;
                linkHasText = false;
            } else if (XMLStreamConstants.END_ELEMENT == event &&
                    reader.hasName() && "a".equals(reader.getLocalName())) {
                if (readingLink) {
                    if (!linkHasText) {
                        throw new RuntimeException("empty <a/>");
                    }
                }
                readingLink = false;
                linkHasText = false;
            } else if ((XMLStreamConstants.CHARACTERS == event) && readingLink) {
                int length = reader.getTextLength();
                if (length > 0) {
                    linkHasText = true;
                }
            }
        }
        assertTrue(foundLink > 0);
    }

    protected String getServletClass() {
        return RootServlet.class.getName();
    }
}
