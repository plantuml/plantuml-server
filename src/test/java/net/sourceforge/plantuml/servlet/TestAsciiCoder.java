package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestAsciiCoder extends WebappTestCase {

    /**
     * Verifies the decoding for the Bob -> Alice sample
     */
    @Test
    public void testBobAliceSampleDiagramDecoding() throws IOException {
        final URL url = new URL(getServerUrl() + "/coder/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get and verify the content
        final String diagram = getContentText(conn);
        Assertions.assertEquals(TestUtils.SEQBOBCODE, diagram);
    }

    /**
     * Verifies the encoding for the Bob -> Alice sample
     */
    @Test
    public void testBobAliceSampleDiagramEncoding() throws IOException {
        final URL url = new URL(getServerUrl() + "/coder");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-type", "text/plain");
        try (final OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
            writer.write(TestUtils.SEQBOBCODE);
            writer.flush();
        }
        // Analyze response
        // HTTP response 200
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        // Verifies the Content-Type header
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content and verify its size
        final String diagram = getContentText(conn.getInputStream());
        Assertions.assertEquals(TestUtils.SEQBOB, diagram);
    }

}
