package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class TestSVG extends WebappTestCase {
    /**
     * Verifies the generation of the SVG for the Bob -> Alice sample
     */
    public void testSimpleSequenceDiagram() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "svg/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000");
        WebResponse response = conversation.getResource( request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals( "Response content type is not SVG", "image/svg+xml", response.getContentType());
        // Get the content and verify its size
        String diagram = response.getText();
        int diagramLen = diagram.length();
        assertTrue( diagramLen > 1700);
        assertTrue( diagramLen < 1800);
    }

}
