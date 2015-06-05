package io.virga.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.servletunit.ServletRunner;
import io.virga.AbstractServletTest;
import io.virga.json.JsonParser;
import io.virga.json.JsonToken;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public abstract class AbstractExceptionTest extends AbstractServletTest {
    protected abstract String getWebXml();

    @BeforeMethod(alwaysRun = true)
    public void createServlet() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(getWebXml());
        sr = new ServletRunner(is);
        sc = sr.newClient();
        sc.setExceptionsThrownOnErrorStatus(false);
        newRequest();
        System.out.flush();
        System.err.flush();
    }

    @BeforeClass(alwaysRun = true)
    public void disableJavascript() {
        HttpUnitOptions.setScriptingEnabled(false);
    }

    @AfterMethod(alwaysRun = true)
    public void flush() {
        System.out.flush();
        System.err.flush();
    }

    @Test
    public void testCommittedResponseError() throws Exception {
        request = new GetMethodWebRequest("http://test.example.com/late");
        getResponse();
        assertHtml();
        String text = response.getText().toLowerCase();
        assertTrue(text.contains("writing response..."));
        assertTrue(text.contains("error 599"));
        assertTrue(text.contains("unit test"));
    }

    @Test
    public void testGetHtml() throws Exception {
        // html is default
        getResponse();
        assertHtml();

        // html can be requested
        newRequest();
        acceptHtml();
        getResponse();
        assertHtml();
        String text = response.getText().toLowerCase();

        assertTrue(text.contains("<html"));
        assertTrue(text.contains("error 501"));
        assertTrue(text.contains("not implemented"));
        assertTrue(text.contains("this is a unit test"));
    }

    @Test
    public void testGetJson() throws Exception {
        acceptJson();
        getResponse();
        assertJson();

        JsonParser parser = getJsonParser();
        JsonToken token;
        int foundCode = 0;
        int foundMessage = 0;
        int foundDetail = 0;
        int foundStackTrace = 0;
        String fieldName = null;
        String fieldValue;
        while ((token = parser.next()) != JsonToken.END) {
            switch (token) {
                case NAME:
                    fieldName = parser.getString();
                    break;
                case VALUE:
                    fieldValue = parser.getString();
                    if ("code".equals(fieldName)) {
                        foundCode++;
                        assertEquals(fieldValue, "501");
                    } else if ("message".equals(fieldName)) {
                        foundMessage++;
                        assertEquals(fieldValue.toLowerCase(), "not implemented");
                    } else if ("detail".equals(fieldName)) {
                        foundDetail++;
                        assertEquals(fieldValue, "this is a unit test");
                    } else if ("stackTrace".equals(fieldName)) {
                        foundStackTrace++;
                    } else {
                        continue;
                    }
                    assertNotNull(fieldValue);
                    break;
            }
        }
        assertEquals(foundCode, 1);
        assertEquals(foundMessage, 1);
        assertEquals(foundDetail, 1);
        assertEquals(foundStackTrace, 1);
    }

    @Test
    public void testGetText() throws Exception {
        acceptText();
        getResponse();
        assertText();
    }

    @Test
    public void testGetXml() throws Exception {
        acceptXml();
        getResponse();
        assertXml();
        XMLStreamReader reader = getXmlStreamReader();
        int foundCode = 0;
        boolean readingCode = false;
        boolean codeHasText = false;
        while (reader.hasNext()) {
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event &&
                    reader.hasName() && "code".equals(reader.getLocalName())) {
                foundCode++;
                readingCode = true;
                codeHasText = false;
            } else if (XMLStreamConstants.END_ELEMENT == event &&
                    reader.hasName() && "code".equals(reader.getLocalName())) {
                if (readingCode) {
                    if (!codeHasText) {
                        throw new RuntimeException("empty <code/>");
                    }
                }
                readingCode = false;
                codeHasText = false;
            } else if ((XMLStreamConstants.CHARACTERS == event) && readingCode) {
                int length = reader.getTextLength();
                if (length > 0) {
                    codeHasText = true;
                }
            }
        }
        assertTrue(foundCode == 1);
    }
}
