package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import java.util.Scanner;

public class TestEPS extends WebappTestCase {
    /**
     * Verifies the generation of the EPS for the Bob -> Alice sample
     */
    public void testSimpleSequenceDiagram() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "eps/" + TestUtils.SEQBOB);
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not EPS", "application/postscript", response.getContentType());
        // Get the content and verify its size
        String diagram = response.getText();
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 10000);
        assertTrue(diagramLen < 12000);
    }
}
