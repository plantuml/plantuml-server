package net.sourceforge.plantuml.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;
import net.sourceforge.plantuml.servlet.server.EmbeddedJettyServer;
import net.sourceforge.plantuml.servlet.server.ExternalServer;
import net.sourceforge.plantuml.servlet.server.ServerUtils;


public abstract class WebappTestCase extends TestCase {

    private final ServerUtils serverUtils;

    public WebappTestCase() {
        this(null);
    }

    public WebappTestCase(String name) {
        super(name);
        // logger = LoggerFactory.getLogger(this.getClass());

        String uri = System.getProperty("system.test.server", "");
        //uri = "http://localhost:8080/plantuml";
        if (!uri.isEmpty()) {
            // mvn test -DskipTests=false -DargLine="-Dsystem.test.server=http://localhost:8080/plantuml"
            // logger.info("Test against external server: " + uri);
            serverUtils = new ExternalServer(uri);
            return;
        }

        // mvn test -DskipTests=false
        // logger.info("Test against embedded jetty server.");
        serverUtils = new EmbeddedJettyServer();
    }

    @Override
    public void setUp() throws Exception {
        serverUtils.startServer();
        // logger.info(getServerUrl());
    }

    @Override
    public void tearDown() throws Exception {
        serverUtils.stopServer();
    }

    protected String getServerUrl() {
        return serverUtils.getServerUrl();
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
        try (
            final InputStreamReader isr = new InputStreamReader(stream);
            final BufferedReader br = new BufferedReader(isr);
        ) {
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString().trim();
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
