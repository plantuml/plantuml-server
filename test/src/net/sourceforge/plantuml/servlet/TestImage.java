package net.sourceforge.plantuml.servlet;

import junit.framework.TestCase;
import com.meterware.httpunit.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class TestImage extends TestCase {
    /**
     * Verifies the generation of the version image from an encoded URL
     */
    public void testVersionImage() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl()+"img/AqijAixCpmC0");
        WebResponse response = conversation.getResource( request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals( "image/png", response.getContentType());
        // Get the image and verify its size 
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
        assertTrue( diagramLen > 10000);
        assertTrue( diagramLen < 20000); 
    }
}
