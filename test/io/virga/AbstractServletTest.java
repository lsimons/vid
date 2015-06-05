/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import io.virga.convert.XML;
import io.virga.json.JsonParser;
import io.virga.json.JsonToken;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class AbstractServletTest {
    protected WebRequest request;
    protected WebResponse response;
    protected ServletUnitClient sc;
    protected ServletRunner sr;

    @BeforeClass(alwaysRun = true)
    public void disableJavascript() {
        HttpUnitOptions.setScriptingEnabled(false);
    }

    @AfterMethod(alwaysRun = true)
    public void flush() {
        System.out.flush();
        System.err.flush();
    }

    protected void acceptHtml() {
        request.setHeaderField("Accept", "text/html");
    }

    protected void acceptJson() {
        request.setHeaderField("Accept", "application/json");
    }

    protected void acceptText() {
        request.setHeaderField("Accept", "text/plain");
    }

    protected void acceptXml() {
        request.setHeaderField("Accept", "application/xml");
    }

    protected void assertCacheControlHeader() {
        assertNotNull(response.getHeaderField("Cache-Control"));
    }

    protected void assertHtml() {
        assertEquals("text/html", response.getContentType());
    }

    protected void assertJson() {
        assertEquals("application/json", response.getContentType());
    }

    protected void assertLinkHeader() {
        assertNotNull(response.getHeaderField("Link"));
    }

    protected void assertObjectResponse() {
        assertUTF8();
        assertLinkHeader();
        assertCacheControlHeader();
        assertVaryAccept();
    }

    protected void assertText() {
        assertEquals("text/plain", response.getContentType());
    }

    protected void assertUTF8() {
        assertEquals("UTF-8", response.getCharacterSet());
    }

    protected void assertVaryAccept() {
        assertTrue(response.getHeaderField("Vary").toLowerCase().contains("accept"));
    }

    protected void assertXml() {
        assertEquals("application/xml", response.getContentType());
    }

    protected JsonParser getJsonParser() throws IOException {
        String body = response.getText();
        return new JsonParser(body);
    }

    protected void getResponse() throws IOException, SAXException {
        response = sc.getResponse(request);
    }

    protected XMLStreamReader getXmlStreamReader() throws IOException, XMLStreamException {
        String body = response.getText();
        return XML.getInputFactory().createXMLStreamReader(new StringReader(body));
    }

    protected void newRequest() {
        request = new GetMethodWebRequest("http://test.example.com/");
    }

    protected void parseJson() throws IOException {
        JsonParser parser = getJsonParser();
        while (parser.next() != JsonToken.END) {
            continue;
        }
    }

    protected void parseXml() throws IOException, XMLStreamException {
        XMLStreamReader reader = getXmlStreamReader();
        while (reader.hasNext()) {
            reader.next();
        }
    }
}
