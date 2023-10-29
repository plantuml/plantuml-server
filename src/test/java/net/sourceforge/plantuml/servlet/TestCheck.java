package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestCheck extends WebappTestCase {

    /**
     * Verifies the generation of a syntax check for the following sample:
     * Bob -> Alice : hello
     */
    @Test
    public void testCorrectSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/check/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content, check its first characters and verify its size
        String checkResult = getContentText(conn);
        Assertions.assertTrue(
            checkResult.startsWith("(2 participants)"),
            "Response content is not starting with (2 participants)"
        );
        int checkLen = checkResult.length();
        Assertions.assertTrue(checkLen > 1);
        Assertions.assertTrue(checkLen < 100);
    }

    /**
     * Check the syntax of an invalid sequence diagram :
     * Bob -
     */
    @Test
    public void testWrongDiagramSyntax() throws IOException {
        final URL url = new URL(getServerUrl() + "/check/SyfFKj050000");
        // Analyze response
        String checkResult = getContentText(url);
        Assertions.assertTrue(checkResult.startsWith("(Error)"), "Response is not an error");
    }

}
