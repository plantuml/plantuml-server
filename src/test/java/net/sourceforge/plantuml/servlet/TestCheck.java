package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class TestCheck extends WebappTestCase {

    /**
     * Verifies the generation of a syntax check for the following sample:
     * Bob -> Alice : hello
     */
    public void testCorrectSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/check/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the content, check its first characters and verify its size
        String checkResult = getContentText(conn);
        assertTrue(
            "Response content is not starting with (2 participants)",
            checkResult.startsWith("(2 participants)")
        );
        int checkLen = checkResult.length();
        assertTrue(checkLen > 1);
        assertTrue(checkLen < 100);
    }

    /**
     * Check the syntax of an invalid sequence diagram :
     * Bob -
     */
    public void testWrongDiagramSyntax() throws IOException {
        final URL url = new URL(getServerUrl() + "/check/SyfFKj050000");
        // Analyze response
        String checkResult = getContentText(url);
        assertTrue("Response is not an error", checkResult.startsWith("(Error)"));
    }

}
