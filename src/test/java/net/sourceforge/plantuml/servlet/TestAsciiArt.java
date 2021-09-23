package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class TestAsciiArt extends WebappTestCase {

    /**
     * Verifies the generation of the ascii art for the Bob -> Alice sample
     */
    public void testSimpleSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 200);
        assertTrue(diagramLen < 250);
    }

}
