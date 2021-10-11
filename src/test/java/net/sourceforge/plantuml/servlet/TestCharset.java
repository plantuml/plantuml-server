package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class TestCharset extends WebappTestCase {

    /**
     * Verifies the preservation of unicode characters for the "Bob -> Alice : hell‽" sample
     */
    public void testUnicodeSupport() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/SyfFKj2rKt3CoKnELR1Io4ZDoNdKi1S0");
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify that the interrobang unicode character is present
        String diagram = getContentText(conn);
        assertTrue("Interrobang unicode character is not preserved", diagram.contains("‽"));
    }

    /**
     * Verifies the preservation of unicode characters for the
     * "participant Bob [[http://www.snow.com/❄]]\nBob -> Alice" sample
     */
    public void testUnicodeInCMap() throws IOException {
        final URL url = new URL(
            getServerUrl() +
            "/map/AqWiAibCpYn8p2jHSCfFKeYEpYWfAR3IroylBzShpiilrqlEpzL_DBSbDfOB9Azhf-2OavcS2W00"
        );
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not TEXT PLAIN or UTF-8",
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify that the snow unicode character is present
        String map = getContentText(conn);
        assertTrue("Snow unicode character is not preserved", map.contains("❄"));
    }

}
