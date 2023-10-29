package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestCharset extends WebappTestCase {

    /**
     * Verifies the preservation of unicode characters for the "Bob -> Alice : hell‽" sample
     */
    @Test
    public void testUnicodeSupport() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/SyfFKj2rKt3CoKnELR1Io4ZDoNdKi1S0");
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content and verify that the interrobang unicode character is present
        String diagram = getContentText(conn);
        Assertions.assertTrue(diagram.contains("‽"), "Interrobang unicode character is not preserved");
    }

    /**
     * Verifies the preservation of unicode characters for the
     * "participant Bob [[http://www.snow.com/❄]]\nBob -> Alice" sample
     */
    @Test
    public void testUnicodeInCMap() throws IOException {
        final URL url = new URL(
            getServerUrl() +
            "/map/AqWiAibCpYn8p2jHSCfFKeYEpYWfAR3IroylBzShpiilrqlEpzL_DBSbDfOB9Azhf-2OavcS2W00"
        );
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content and verify that the snow unicode character is present
        String map = getContentText(conn);
        Assertions.assertTrue(map.contains("❄"), "Snow unicode character is not preserved");
    }

}
