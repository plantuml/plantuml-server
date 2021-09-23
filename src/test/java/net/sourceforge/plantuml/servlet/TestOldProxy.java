package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class TestOldProxy extends WebappTestCase {

    /**
     * Verifies the proxified reception of the default Bob and Alice diagram
     */
    public void testDefaultProxy() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy/" + getServerUrl() + "/resource/test2diagrams.txt");
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not PNG",
            "image/png",
            conn.getContentType().toLowerCase()
        );
        // Get the image and verify its size (~2000 bytes)
        byte[] inMemoryImage = getContentAsBytes(conn);
        int diagramLen = inMemoryImage.length;
        assertTrue(diagramLen > 2000);
        assertTrue(diagramLen < 3000);
    }

    public void testProxyWithFormat() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy/svg/" + getServerUrl() + "/resource/test2diagrams.txt");
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not SVG",
            "image/svg+xml",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 1000);
        assertTrue(diagramLen < 3000);
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    public void testInvalidUrl() throws IOException {
        final URL url = new URL(getServerUrl() + "/proxy/invalidURL");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Get the content and verify its size
        assertEquals(
            "Response is not empty",
            0,
            conn.getContentLength()
        );
        // Check if status code is 400
        assertEquals(
            "Bad HTTP status received",
            400,
            conn.getResponseCode()
        );
    }

}
