package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestOldProxy extends WebappTestCase {

    private static final String TEST_RESOURCE = "test2diagrams.txt";

    /**
     * Verifies the proxified reception of the default Bob and Alice diagram
     */
    @Test
    public void testDefaultProxy() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy/" + getTestResourceUrl(TEST_RESOURCE));
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "image/png",
            conn.getContentType().toLowerCase(),
            "Response content type is not PNG"
        );
        // Get the image and verify its size (~2000 bytes)
        byte[] inMemoryImage = getContentAsBytes(conn);
        int diagramLen = inMemoryImage.length;
        Assertions.assertTrue(diagramLen > 100);
    }

    /**
     * Verifies the proxified reception of the default Bob and Alice diagram in a specific format (SVG)
     */
    @Test
    public void testProxyWithFormat() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy/svg/" + getTestResourceUrl(TEST_RESOURCE));
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "image/svg+xml",
            conn.getContentType().toLowerCase(),
            "Response content type is not SVG"
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        Assertions.assertTrue(diagramLen > 1000);
        Assertions.assertTrue(diagramLen < 6000);
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    @Test
    public void testInvalidUrl() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy/invalidURL");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Check if status code is 400
        Assertions.assertEquals(400, conn.getResponseCode(), "Bad HTTP status received");
        // Check error message
        Assertions.assertTrue(
            getContentText(conn.getErrorStream()).contains("URL malformed."),
            "Response is not malformed URL"
        );
    }

}
