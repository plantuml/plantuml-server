package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestPDF extends WebappTestCase {

    /**
     * Verifies the generation of the PDF for the Bob -> Alice sample
     */
    @Test
    public void testSimpleSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/pdf/" + TestUtils.SEQBOB);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "application/pdf",
            conn.getContentType().toLowerCase(),
            "Response content type is not PDF"
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        Assertions.assertTrue(diagramLen > 1500);
        Assertions.assertTrue(diagramLen < 2000);
    }

}
