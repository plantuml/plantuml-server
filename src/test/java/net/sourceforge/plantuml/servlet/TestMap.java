package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class TestMap extends WebappTestCase {

    /**
     * Verifies the generation of the MAP for the following sample:
     *
     * participant Bob [[http://www.yahoo.com]]
     * Bob -> Alice : [[http://www.google.com]] hello
     */
    public void testSimpleSequenceDiagram() throws IOException {
        final URL url = new URL(
            getServerUrl() +
            "/map/AqWiAibCpYn8p2jHSCfFKeYEpYWfAR3IroylBzUhJCp8pzTBpi-DZUK2IUhQAJZcP2QdAbYXgalFpq_FIOKeLCX8pSd91m00"
        );
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the content, check its first characters and verify its size
        String diagram = getContentText(conn);
        assertTrue(
            "Response content is not starting with <map",
            diagram.startsWith("<map")
        );
        assertTrue(
            "Response content (2. line) is not starting with <area",
            diagram.split("\\n", 2)[1].startsWith("<area")
        );
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 200);
        assertTrue(diagramLen < 300);
    }

    /**
     * Check the content of the MAP for the sequence diagram sample
     * Verify structure of the area tags
     */
    public void testSequenceDiagramContent() throws IOException {
        final URL url = new URL(
            getServerUrl() +
            "/map/AqWiAibCpYn8p2jHSCfFKeYEpYWfAR3IroylBzUhJCp8pzTBpi-DZUK2IUhQAJZcP2QdAbYXgalFpq_FIOKeLCX8pSd91m00"
        );
        // Analyze response
        // Get the data contained in the XML
        String map = getContentText(url);
        // Verify shape:
        // <map id="..." name="...">
        // <area shape="..." id="..." href="..." ... />
        // <area shape="..." id="..." href="..." ... />
        // </map>
        assertTrue(
            "Response doesn't match shape",
            map.matches("^<map id=\".+\" name=\".+\">\n(<area shape=\".+\" id=\".+\" href=\".+\".*/>\n){2}</map>\n*$")
        );
    }

    /**
     * Check the empty MAP of a sequence diagram without link
     * This test uses the simple Bob -> Alice
     */
    public void testSequenceDiagramWithoutLink() throws IOException {
        final URL url = new URL(getServerUrl() + "/map/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the data contained in the XML
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertEquals(0, diagramLen);
    }

}
