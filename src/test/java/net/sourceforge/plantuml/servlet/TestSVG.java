package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import java.util.Scanner;

public class TestSVG extends WebappTestCase {
    /**
     * Verifies the generation of the SVG for the Bob -> Alice sample
     */
    public void testSimpleSequenceDiagram() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "svg/" + TestUtils.SEQBOB);
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not SVG", "image/svg+xml", response.getContentType());
        // Get the content and verify its size
        String diagram = response.getText();
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 1000);
        assertTrue(diagramLen < 3000);
    }

    /**
     * Check the content of the SVG
     */
    public void testSequenceDiagramContent() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "svg/" + TestUtils.SEQBOB);
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Get the data contained in the XML
        Scanner s = new Scanner(response.getInputStream()).useDelimiter("(<([^<>]+)>)+");
        String token;
        int bobCounter = 0, aliceCounter = 0;
        while (s.hasNext()) {
            token = s.next();
            if (token.startsWith("Bob")) {
                bobCounter++;
            }
            if (token.startsWith("Alice")) {
                aliceCounter++;
            }
        }
        assertTrue(bobCounter == 2);
        assertTrue(aliceCounter == 2);
    }
}
