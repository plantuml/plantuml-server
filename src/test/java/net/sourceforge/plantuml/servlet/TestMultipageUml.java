package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestMultipageUml extends WebappTestCase {

    /**
     * Verifies that an multipage diagram renders correct given index (PNG).
     */
    @Test
    public void testPngIndexPage() throws IOException {
        final URL url = new URL(getServerUrl() + "/png/1/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "image/png",
            conn.getContentType().toLowerCase(),
            "Response content type is not PNG"
        );
        // Get the image and verify its size
        byte[] inMemoryImage = getContentAsBytes(conn);
        int diagramLen = inMemoryImage.length;  // 7525
        Assertions.assertTrue(diagramLen > 6000);
        Assertions.assertTrue(diagramLen < 9000);
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified (PNG).
     */
    @Test
    public void testPngIndexPageNoIndex() throws IOException {
        final URL url = new URL(getServerUrl() + "/png/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "image/png",
            conn.getContentType().toLowerCase(),
            "Response content type is not PNG"
        );
        // Get the image and verify its size
        byte[] inMemoryImage = getContentAsBytes(conn);
        int diagramLen = inMemoryImage.length;  // 4196
        Assertions.assertTrue(diagramLen > 3000);
        Assertions.assertTrue(diagramLen < 5000);
    }

    /**
     * Verifies that an multipage diagram renders correct given index (SVG).
     */
    @Test
    public void testSvgIndexPage() throws IOException {
        final URL url = new URL(getServerUrl() + "/svg/1/" + TestUtils.SEQMULTIPAGE);
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
        Assertions.assertTrue(diagramLen > 4500);
        Assertions.assertTrue(diagramLen < 6000);
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified (SVG).
     */
    @Test
    public void testSvgIndexPageNoIndex() throws IOException {
        final URL url = new URL(getServerUrl() + "/svg/" + TestUtils.SEQMULTIPAGE);
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
        Assertions.assertTrue(diagramLen < 4000);
    }

    /**
     * Verifies that an multipage diagram renders correct given index (AsciiArt).
     */
    @Test
    public void testAsciiArtIndexPage() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/1/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        Assertions.assertNotEquals(0, diagramLen);
        // BUG/Missing Feature: plantuml renders always whole AsciiArt diagram
        //Assertions.assertTrue(diagramLen > ??);
        //Assertions.assertTrue(diagramLen < ??);
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified (AsciiArt).
     */
    @Test
    public void testAsciiArtIndexPageNoIndex() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        Assertions.assertNotEquals(0, diagramLen);
        // BUG/Missing Feature: plantuml renders always whole AsciiArt diagram
        //Assertions.assertTrue(diagramLen > ??);
        //Assertions.assertTrue(diagramLen < ??);
    }

    /**
     * Verifies that an multipage diagram renders correct given index (Map).
     */
    @Test
    public void testMapIndexPage() throws IOException {
        final URL url = new URL(getServerUrl() + "/map/1/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the data contained in the XML
        String map = getContentText(url);
        // map contains "tel:0123456789"
        Assertions.assertTrue(
            map.contains("tel:0123456789"),
            "Response does not contain 'tel:0123456789'"
        );
        // Verify shape:
        // <map id="..." name="...">
        // <area shape="..." id="..." href="..." ... />
        // </map>
        Assertions.assertTrue(
            map.matches("^<map id=\".+\" name=\".+\">\n(<area shape=\".+\" id=\".+\" href=\".+\".*/>\n)</map>\n*$"),
            "Response doesn't match shape"
        );
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified (Map).
     */
    @Test
    public void testMapIndexPageNoIndex() throws IOException {
        final URL url = new URL(getServerUrl() + "/map/" + TestUtils.SEQMULTIPAGE);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
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
