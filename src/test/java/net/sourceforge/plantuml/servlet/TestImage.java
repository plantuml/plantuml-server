package net.sourceforge.plantuml.servlet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class TestImage extends WebappTestCase {
    /**
     * Verifies the generation of the version image from an encoded URL
     */
    public void testVersionImage() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "png/" + TestUtils.VERSION);
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not PNG", "image/png", response.getContentType());
        // Get the image and verify its size
        InputStream responseStream = response.getInputStream();
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while ((n = responseStream.read(buf)) != -1) {
            imageStream.write(buf, 0, n);
        }
        imageStream.close();
        byte[] inMemoryImage = imageStream.toByteArray();
        int diagramLen = inMemoryImage.length;
        assertTrue(diagramLen > 10000);
        assertTrue(diagramLen < 20000);
        responseStream.close();
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    public void testDiagramHttpHeader() throws Exception {
        WebConversation conversation = new WebConversation();
        // Bob -> Alice : hello
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "png/" + TestUtils.SEQBOB);
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not PNG", "image/png", response.getContentType());
        // Verifies the availability of the Expires entry in the response header
        assertNotNull(response.getHeaderField("Expires"));
        // Verifies the availability of the Last-Modified entry in the response header
        assertNotNull(response.getHeaderField("Last-Modified"));
        // Verifies the Last-Modified value is in the past
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.ENGLISH);
        Date lastModified = format.parse(response.getHeaderField("Last-Modified"));
        assertTrue("Last-Modified is not in the past", lastModified.before(new Date()));
        // Consume the response
        InputStream responseStream = response.getInputStream();
        while (responseStream.read() != -1) {
            // Do nothing
        }
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    public void testOldImgURL() throws Exception {
        WebConversation conversation = new WebConversation();
        // Bob -> Alice : hello
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "img/" + TestUtils.SEQBOB);
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not PNG", "image/png", response.getContentType());
        // Consume the response
        InputStream responseStream = response.getInputStream();
        while (responseStream.read() != -1) {
            // Do nothing
        }
    }
}
