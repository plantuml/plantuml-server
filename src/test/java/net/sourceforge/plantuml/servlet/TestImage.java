package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TestImage extends WebappTestCase {

    /**
     * Verifies the generation of the version image from an encoded URL
     */
    public void testVersionImage() throws IOException {
        final URL url = new URL(getServerUrl() + "/png/" + TestUtils.VERSION);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not PNG",
            "image/png",
            conn.getContentType().toLowerCase()
        );
        // Get the image and verify its size
        byte[] inMemoryImage = getContentAsBytes(conn);
        int diagramLen = inMemoryImage.length;
        assertTrue(diagramLen > 10000);
        assertTrue(diagramLen < 20000);
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    public void testDiagramHttpHeader() throws IOException, ParseException {
        final URL url = new URL(getServerUrl() + "/png/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not PNG",
            "image/png",
            conn.getContentType().toLowerCase()
        );
        // Verifies the availability of the Expires entry in the response header
        assertNotNull(conn.getHeaderField("Expires"));
        // Verifies the availability of the Last-Modified entry in the response header
        assertNotNull(conn.getHeaderField("Last-Modified"));
        // Verifies the Last-Modified value is in the past
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.ENGLISH);
        Date lastModified = format.parse(conn.getHeaderField("Last-Modified"));
        assertTrue("Last-Modified is not in the past", lastModified.before(new Date()));
        // Consume the response but do nothing with it
        getContentAsBytes(conn);
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    public void testOldImgURL() throws IOException {
        final URL url = new URL(getServerUrl() + "/img/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not PNG",
            "image/png",
            conn.getContentType().toLowerCase()
        );
        // Consume the response but do nothing with it
        getContentAsBytes(conn);
    }

}
