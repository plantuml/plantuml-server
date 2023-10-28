package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestMap extends WebappTestCase {

    /**
     * Verifies the generation of the MAP for the following sample:
     *
     * participant Bob [[http://www.yahoo.com]]
     * Bob -> Alice : [[http://www.google.com]] hello
     */
    @Test
    public void testSimpleSequenceDiagram() throws IOException {
        final URL url = new URL(
            getServerUrl() +
            "/map/AqWiAibCpYn8p2jHSCfFKeYEpYWfAR3IroylBzUhJCp8pzTBpi-DZUK2IUhQAJZcP2QdAbYXgalFpq_FIOKeLCX8pSd91m00"
        );
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content, check its first characters and verify its size
        String diagram = getContentText(conn);
        Assertions.assertTrue(
            diagram.startsWith("<map"),
            "Response content is not starting with <map"
        );
        Assertions.assertTrue(
            diagram.split("\\n", 2)[1].startsWith("<area"),
            "Response content (2. line) is not starting with <area"
        );
        int diagramLen = diagram.length();
        Assertions.assertTrue(diagramLen > 200);
        Assertions.assertTrue(diagramLen < 300);
    }

    /**
     * Check the content of the MAP for the sequence diagram sample
     * Verify structure of the area tags
     */
    @Test
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
        Assertions.assertTrue(
            map.matches("^<map id=\".+\" name=\".+\">\n(<area shape=\".+\" id=\".+\" href=\".+\".*/>\n){2}</map>\n*$"),
            "Response doesn't match shape"
        );
    }

    /**
     * Check the empty MAP of a sequence diagram without link
     * This test uses the simple Bob -> Alice
     */
    @Test
    public void testSequenceDiagramWithoutLink() throws IOException {
        final URL url = new URL(getServerUrl() + "/map/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the data contained in the XML
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        Assertions.assertEquals(0, diagramLen);
    }

}
