package net.sourceforge.plantuml.servlet;

import junit.framework.TestCase;
import com.meterware.httpunit.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class TestProxy extends TestCase {
    /**
     * Verifies the proxified reception of the default Bob and Alice diagram
     */
    public void testDefaultProxy() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl()+"proxy/"
                +TestUtils.getServerUrl()+"welcome");
        WebResponse response = conversation.getResource( request);
        // Analyze response
        // Verifies the Content-Type header
        //assertEquals( "Response content type is not PNG", "image/png", response.getContentType());
        // Get the image and verify its size (~1533 bytes)
        InputStream responseStream = response.getInputStream();
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while( ( n = responseStream.read( buf)) != -1) {
            imageStream.write( buf, 0, n);
        }
        imageStream.close();
        responseStream.close();
        byte[] inMemoryImage = imageStream.toByteArray();
        int diagramLen = inMemoryImage.length;
        assertTrue( diagramLen > 1500);
        assertTrue( diagramLen < 1600); 
    }

    /**
     * Verifies that the HTTP header of a diagram incites the browser to cache it.
     */
    public void testInvalidUrl() throws Exception {
        WebConversation conversation = new WebConversation();
        // Try to proxify an invalid address 
        WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl()+"proxy/invalidURL");
        WebResponse response = conversation.getResource( request);
        // Analyze response, it must be the empty form
        // Verifies the Content-Type header
        assertEquals( "Response content type is not HTML", "text/html", response.getContentType());
        WebForm forms[] = response.getForms();
        assertEquals( 2, forms.length );
    }
}
