package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class TestAsciiArt extends WebappTestCase {

    /**
     * Verifies the generation of the ascii art for the Bob -> Alice sample
     */
    public void testSimpleSequenceDiagram() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "txt/" + TestUtils.SEQBOB);
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not TEXT PLAIN", "text/plain", response.getContentType());
        assertEquals("Response character set is not UTF-8", "UTF-8", response.getCharacterSet());
        // Get the content and verify its size
        String diagram = response.getText();
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 200);
        assertTrue(diagramLen < 250);
    }

}
