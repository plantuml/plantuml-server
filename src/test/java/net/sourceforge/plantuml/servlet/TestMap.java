package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class TestMap extends WebappTestCase {
    /**
     * Verifies the generation of the MAP for the following sample:
     *
     * participant Bob [[http://www.yahoo.com]]
     * Bob -> Alice : [[http://www.google.com]] hello
     */
    public void testSimpleSequenceDiagram() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl()
            + "map/AqWiAibCpYn8p2jHSCfFKeYEpYWfAR3IroylBzUhJCp8pzTBpi-DZUK2IUhQAJZcP2QdAbYXgalFpq_FIOKeLCX8pSd91m00");
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not TEXT PLAIN", "text/plain", response.getContentType());
        assertEquals("Response character set is not UTF-8", "UTF-8", response.getCharacterSet());
        // Get the content, check its first characters and verify its size
        String diagram = response.getText();
        assertTrue("Response content is not starting with <area", diagram.startsWith("<area"));
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 200);
        assertTrue(diagramLen < 300);
    }

    /**
     * Check the content of the MAP for the sequence diagram sample
     * Verify structure of the area tags
     */
    public void testSequenceDiagramContent() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl()
            + "map/AqWiAibCpYn8p2jHSCfFKeYEpYWfAR3IroylBzUhJCp8pzTBpi-DZUK2IUhQAJZcP2QdAbYXgalFpq_FIOKeLCX8pSd91m00");
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Get the data contained in the XML
        String map = response.getText();
        assertTrue("Response is not a list of tags", map.matches("(<([^<>]+)>)+"));
        assertTrue("Response doesn't contain the area structure",
                map.matches(".*(area shape=\".+\" id=\".+\" href=\".+\").*"));
    }

    /**
     * Check the empty MAP of a sequence diagram without link
     * This test uses the simple Bob -> Alice
     */
    public void testSequenceDiagramWithoutLink() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "map/" + TestUtils.SEQBOB);
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not TEXT PLAIN", "text/plain", response.getContentType());
        // Get the content, check it's an empty response
        String diagram = response.getText();
        int diagramLen = diagram.length();
        assertEquals(0, diagramLen);
    }
}
