package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestAsciiArt extends WebappTestCase {

    /**
     * Verifies the generation of the ascii art for the Bob -> Alice sample
     */
    @Test
    public void testSimpleSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        Assertions.assertTrue(diagramLen > 200);
        Assertions.assertTrue(diagramLen < 250);
    }

}
