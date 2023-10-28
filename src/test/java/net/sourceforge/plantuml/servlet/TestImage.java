package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestImage extends WebappTestCase {

    /**
     * Verifies the generation of the version image from an encoded URL
     */
    @Test
    public void testVersionImage() throws IOException {
        final URL url = new URL(getServerUrl() + "/png/" + TestUtils.VERSION);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "image/png",
            conn.getContentType().toLowerCase(),
            "Response content type is not PNG"
        );
        // Get the image and verify its size
        byte[] inMemoryImage = getContentAsBytes(conn);
        int diagramLen = inMemoryImage.length;
        Assertions.assertTrue(diagramLen > 10000);
        Assertions.assertTrue(diagramLen < 20000);
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    @Test
    public void testDiagramHttpHeader() throws IOException, ParseException {
        final URL url = new URL(getServerUrl() + "/png/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "image/png",
            conn.getContentType().toLowerCase(),
            "Response content type is not PNG"
        );
        // Verifies the availability of the Expires entry in the response header
        Assertions.assertNotNull(conn.getHeaderField("Expires"));
        // Verifies the availability of the Last-Modified entry in the response header
        Assertions.assertNotNull(conn.getHeaderField("Last-Modified"));
        // Verifies the Last-Modified value is in the past
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.ENGLISH);
        Date lastModified = format.parse(conn.getHeaderField("Last-Modified"));
        Assertions.assertTrue(lastModified.before(new Date()), "Last-Modified is not in the past");
        // Consume the response but do nothing with it
        getContentAsBytes(conn);
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    @Test
    public void testOldImgURL() throws IOException {
        final URL url = new URL(getServerUrl() + "/img/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "image/png",
            conn.getContentType().toLowerCase(),
            "Response content type is not PNG"
        );
        // Consume the response but do nothing with it
        getContentAsBytes(conn);
    }

}
