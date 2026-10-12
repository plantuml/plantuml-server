package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestProxy extends WebappTestCase {

    private static final String TEST_RESOURCE = "test2diagrams.txt";

    /**
     * Verifies the proxified reception of the default Bob and Alice diagram
     */
    @Test
    public void testDefaultProxy() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy?src=" + getTestResourceUrl(TEST_RESOURCE));
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
     * Verifies the proxified reception of the default Bob and Alice diagram with defined format.
     */
    @Test
    public void testProxyWithFormat() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy?fmt=svg&src=" + getTestResourceUrl(TEST_RESOURCE));
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
        Assertions.assertTrue(diagramLen > 1500);
        Assertions.assertTrue(diagramLen < 6000);
    }

    /**
     * Verifies the proxified reception of the default Bob and Alice diagram with defined format and format (idx=0).
     */
    @Test
    public void testProxyWithFormatIdx0() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy?fmt=svg&idx=0&src=" + getTestResourceUrl(TEST_RESOURCE));
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
        Assertions.assertTrue(diagramLen > 1500);
        Assertions.assertTrue(diagramLen < 6000);
    }

    /**
     * Verifies the proxified reception of the default Bob and Alice diagram with defined format and format (idx=1).
     */
    @Test
    public void testProxyWithFormatIdx1() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy?fmt=svg&idx=1&src=" + getTestResourceUrl(TEST_RESOURCE));
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
        Assertions.assertTrue(diagramLen > 3000);
        Assertions.assertTrue(diagramLen < 12000);
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    @Test
    public void testInvalidUrl() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy?src=invalidURL");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response, it must be HTTP error 400
        Assertions.assertEquals(400, conn.getResponseCode(), "Bad HTTP status received");
        // Check error message
        Assertions.assertTrue(
            getContentText(conn.getErrorStream()).contains("URL malformed."),
            "Response is not malformed URL"
        );
    }

}
