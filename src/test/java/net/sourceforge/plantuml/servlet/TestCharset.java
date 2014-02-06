package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class TestCharset extends WebappTestCase {

    /**
     * Verifies the preservation of unicode characters for the "Bob -> Alice : hell‽" sample
     */
    public void testUnicodeSupport() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "txt/SyfFKj2rKt3CoKnELR1Io4ZDoNdKi1S0");
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not TEXT PLAIN", "text/plain", response.getContentType());
        // Get the content and verify that the interrobang unicode character is present
        String diagram = response.getText();
        assertTrue("Interrobang unicode character is not preserved", diagram.contains("‽"));
    }

    /**
     * Verifies the preservation of unicode characters for the
     * "participant Bob [[http://www.snow.com/❄]]\nBob -> Alice" sample
     */
    public void testUnicodeInCMap() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl()
                + "map/AqWiAibCpYn8p2jHSCfFKeYEpYWfAR3IroylBzShpiilrqlEpzL_DBSbDfOB9Azhf-2OavcS2W00");
        WebResponse response = conversation.getResource(request);
        // Analyze response
        // Verifies the Content-Type header
        assertEquals("Response content type is not TEXT PLAIN", "text/plain", response.getContentType());
        // Get the content and verify that the snow unicode character is present
        String map = response.getText();
        assertTrue("Snow unicode character is not preserved", map.contains("❄"));
    }


}
