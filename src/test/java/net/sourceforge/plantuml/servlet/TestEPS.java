package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class TestEPS extends WebappTestCase {

    /**
     * Verifies the generation of the EPS for the Bob -> Alice sample
     */
    public void testSimpleSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/eps/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not EPS",
            "application/postscript",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 10000);
        assertTrue(diagramLen < 12000);
    }

}
