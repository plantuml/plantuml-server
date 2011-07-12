package net.sourceforge.plantuml.servlet;

import junit.framework.TestCase;
import com.meterware.httpunit.*;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class TestSVG extends TestCase {
    /**
     * Verifies the generation of the SVG for the Bob -> Alice sample
     */
    public void testSimpleSequenceDiagram() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl()+"svg/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000");
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
