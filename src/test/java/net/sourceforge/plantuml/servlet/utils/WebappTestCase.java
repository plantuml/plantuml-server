package net.sourceforge.plantuml.servlet.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import net.sourceforge.plantuml.servlet.server.EmbeddedJettyServer;
import net.sourceforge.plantuml.servlet.server.ExternalServer;
import net.sourceforge.plantuml.servlet.server.ServerUtils;


public abstract class WebappTestCase {

    private final ServerUtils serverUtils;

    public WebappTestCase() {
        this(null);
    }

    public WebappTestCase(String name) {
        String uri = System.getProperty("system.test.server", "");
        if (!uri.isEmpty()) {
            // mvn test -DskipTests=false -DargLine="-Dsystem.test.server=http://localhost:8080/plantuml"
            serverUtils = new ExternalServer(uri);
            return;
        }
        // mvn test -DskipTests=false
        serverUtils = new EmbeddedJettyServer();
    }

    @BeforeEach
    public void setUp() throws Exception {
        serverUtils.startServer();
    }

    @AfterEach
    public void tearDown() throws Exception {
        serverUtils.stopServer();
    }

    public String getServerUrl() {
        return serverUtils.getServerUrl();
    }

    public String getTestResourceUrl(String resource) {
        // NOTE: [Old]ProxyServlet.forbiddenURL do not allow URL with IP-Addresses or localhost.
        String serverUrl = getServerUrl().replace("/localhost", "/test.localhost");
        return serverUrl + "/resource/test/" + resource;
    }

    public String getContentText(final URL url) throws IOException {
        try (final InputStream responseStream = url.openStream()) {
            return getContentText(responseStream);
        }
    }

    public String getContentText(final URLConnection conn) throws IOException {
        try (final InputStream responseStream = conn.getInputStream()) {
            return getContentText(responseStream);
        }
    }

    public String getContentText(final InputStream stream) throws IOException {
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }

    public byte[] getContentAsBytes(final URL url) throws IOException {
        try (final InputStream responseStream = url.openStream()) {
            return getContentAsBytes(responseStream);
        }
    }

    public byte[] getContentAsBytes(final URLConnection conn) throws IOException {
        try (final InputStream responseStream = conn.getInputStream()) {
            return getContentAsBytes(responseStream);
        }
    }

    public byte[] getContentAsBytes(final InputStream stream) throws IOException {
        try (final ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int n = 0;
            while ((n = stream.read(buf)) != -1) {
                byteStream.write(buf, 0, n);
            }
            return byteStream.toByteArray();
        }
    }
}
