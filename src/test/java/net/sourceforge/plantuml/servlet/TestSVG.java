package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


public class TestSVG extends WebappTestCase {

    /**
     * Verifies the generation of the SVG for the Bob -> Alice sample
     */
    public void testSimpleSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/svg/" + TestUtils.SEQBOB);
        final URLConnection conn = url.openConnection();
        // Analyze response
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not SVG",
            "image/svg+xml",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn);
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 1000);
        assertTrue(diagramLen < 3000);
    }

    /**
     * Verifies the generation of the SVG for the Bob -> Alice sample
     */
    public void testPostedSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/svg");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-type", "text/plain");
        try (final OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
            writer.write("@startuml\nBob -> Alice\n@enduml");
            writer.flush();
        }
        // Analyze response
        // HTTP response 200
        assertEquals(
            "Bad HTTP status received",
            200,
            conn.getResponseCode()
        );
        // Verifies the Content-Type header
        assertEquals(
            "Response content type is not SVG",
            "image/svg+xml",
            conn.getContentType().toLowerCase()
        );
        // Get the content and verify its size
        String diagram = getContentText(conn.getInputStream());
        int diagramLen = diagram.length();
        assertTrue(diagramLen > 1000);
        assertTrue(diagramLen < 3000);
    }

    /**
     * Verifies the generation of the SVG for the Bob -> Alice sample
     */
    public void testPostedInvalidSequenceDiagram() throws IOException {
        final URL url = new URL(getServerUrl() + "/svg");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-type", "text/plain");
        try (final OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
            writer.write("@startuml\n[Bob\n@enduml");
            writer.flush();
        }
        // Analyze response
        // HTTP response 400
        assertEquals(
            "Bad HTTP status received",
            400,
            conn.getResponseCode()
        );
    }

    /**
     * Check the content of the SVG
     */
    public void testSequenceDiagramContent() throws IOException {
        final URL url = new URL(getServerUrl() + "/svg/" + TestUtils.SEQBOB);
        // Analyze response
        // Get the data contained in the XML
        try (
            final InputStream responseStream = url.openStream();
            final Scanner scanner = new Scanner(responseStream).useDelimiter("(<([^<>]+)>)+")
        ) {
            String token;
            int bobCounter = 0;
            int aliceCounter = 0;
            while (scanner.hasNext()) {
                token = scanner.next();
                if (token.startsWith("Bob")) {
                    bobCounter++;
                }
                if (token.startsWith("Alice")) {
                    aliceCounter++;
                }
            }
            assertTrue(bobCounter == 2);
            assertTrue(aliceCounter == 2);
        }
    }

}
