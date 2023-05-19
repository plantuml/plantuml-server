package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestDependencies extends WebappTestCase {

    /**
     * Verifies that Graphviz is installed and can be found
     */
    @Test
    @Tag("graphviz-test")
    public void testGraphviz() throws IOException {
        final URL url = new URL(getServerUrl() + "/txt/" + TestUtils.VERSION);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
        Assertions.assertEquals(
            "text/plain;charset=utf-8",
            conn.getContentType().toLowerCase(),
            "Response content type is not TEXT PLAIN or UTF-8"
        );
        // Get the content and check installation status
        String diagram = getContentText(conn);
        Assertions.assertTrue(
            diagram.contains("Installation seems OK. File generation OK"),
            "Version diagram was:\n" + diagram
        );
    }

    /**
     * Verifies that the Monaco Editor webjar can be loaded
     */
    @Test
    public void testMonacoEditorWebJar() throws IOException {
        final URL url = new URL(getServerUrl() + "/webjars/monaco-editor/0.36.1/min/vs/loader.js");
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Analyze response
        // Verifies HTTP status code and the Content-Type
        Assertions.assertEquals(200, conn.getResponseCode(), "Bad HTTP status received");
    }

}
