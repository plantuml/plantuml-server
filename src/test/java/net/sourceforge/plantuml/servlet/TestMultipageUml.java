package net.sourceforge.plantuml.servlet;

import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestMultipageUml extends WebappTestCase {

    /**
     * Verifies that an multipage diagram renders correct given index (PNG).
     */
    public void testPngIndexPage() throws IOException {
        final URL url = new URL(getServerUrl() + "/png/1/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        assertEquals("Bad HTTP status received", 200, conn.getResponseCode());
        assertEquals(
            "Response content type is not PNG",
            "image/png",
            conn.getContentType().toLowerCase()
        );
        // Get the image and verify its size
        byte[] inMemoryImage = getContentAsBytes(conn);
        int diagramLen = inMemoryImage.length;  // 7525
        assertTrue(diagramLen > 6000);
        assertTrue(diagramLen < 9000);
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified (PNG).
     */
    public void testPngIndexPageNoIndex() throws IOException {
        final URL url = new URL(getServerUrl() + "/png/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        assertEquals("Bad HTTP status received", 200, conn.getResponseCode());
        assertEquals(
            "Response content type is not PNG",
            "image/png",
            conn.getContentType().toLowerCase()
        );
        // Get the image and verify its size
        byte[] inMemoryImage = getContentAsBytes(conn);
        int diagramLen = inMemoryImage.length;  // 4196
        assertTrue(diagramLen > 3000);
        assertTrue(diagramLen < 5000);
    }

    /**
     * Verifies that an multipage diagram renders correct given index (SVG).
     */
    public void testSvgIndexPage() throws IOException {
        final URL url = new URL(getServerUrl() + "/svg/1/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        assertEquals("Bad HTTP status received", 200, conn.getResponseCode());
        assertEquals(
            "Response content type is not SVG",
            "image/svg+xml",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 4500);
        assertTrue(diagramLen < 6000);
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified (SVG).
     */
    public void testSvgIndexPageNoIndex() throws IOException {
        final URL url = new URL(getServerUrl() + "/svg/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        assertEquals("Bad HTTP status received", 200, conn.getResponseCode());
        assertEquals(
            "Response content type is not SVG",
            "image/svg+xml",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 1500);
        assertTrue(diagramLen < 4000);
    }

    /**
     * Verifies that an multipage diagram renders correct given index (AsciiArt).
     */
    public void testAsciiArtIndexPage() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/1/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        assertEquals("Bad HTTP status received", 200, conn.getResponseCode());
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertNotEquals(0, diagramLen);
        // BUG/Missing Feature: plantuml renders always whole AsciiArt diagram
        //assertTrue(diagramLen > ??);
        //assertTrue(diagramLen < ??);
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified (AsciiArt).
     */
    public void testAsciiArtIndexPageNoIndex() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        assertEquals("Bad HTTP status received", 200, conn.getResponseCode());
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertNotEquals(0, diagramLen);
        // BUG/Missing Feature: plantuml renders always whole AsciiArt diagram
        //assertTrue(diagramLen > ??);
        //assertTrue(diagramLen < ??);
    }

    /**
     * Verifies that an multipage diagram renders correct given index (Map).
     */
    public void testMapIndexPage() throws IOException {
        final URL url = new URL(getServerUrl() + "/map/1/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        assertEquals("Bad HTTP status received", 200, conn.getResponseCode());
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the data contained in the XML
        String map = getContentText(url);
        // map contains "tel:0123456789"
        assertTrue(
            "Response does not contain 'tel:0123456789'",
            map.contains("tel:0123456789")
        );
        // Verify shape:
        // <map id="..." name="...">
        // <area shape="..." id="..." href="..." ... />
        // </map>
        assertTrue(
            "Response doesn't match shape",
            map.matches("^<map id=\".+\" name=\".+\">\n(<area shape=\".+\" id=\".+\" href=\".+\".*/>\n)</map>\n*$")
        );
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified (Map).
     */
    public void testMapIndexPageNoIndex() throws IOException {
        final URL url = new URL(getServerUrl() + "/map/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        assertEquals("Bad HTTP status received", 200, conn.getResponseCode());
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
