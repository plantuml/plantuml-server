package net.sourceforge.plantuml.servlet.server;

import java.util.EnumSet;

import org.eclipse.jetty.http.UriCompliance;
import org.eclipse.jetty.http.UriCompliance.Violation;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

public class EmbeddedJettyServer implements ServerUtils {

    private static final String contextPath = "/plantuml";
    private Server server;

    public EmbeddedJettyServer() {
        server = new Server();

        ServerConnector connector = new ServerConnector(server);
        // Proxy and OldProxy need empty path segments support in URIs
        // Hence: allow AMBIGUOUS_EMPTY_SEGMENT
        UriCompliance uriCompliance = UriCompliance.from(EnumSet.of(Violation.AMBIGUOUS_EMPTY_SEGMENT));
        connector.getConnectionFactory(HttpConnectionFactory.class)
            .getHttpConfiguration()
            .setUriCompliance(uriCompliance);
        server.addConnector(connector);

        // PlantUML server web application
        WebAppContext context = new WebAppContext(server, "src/main/webapp", EmbeddedJettyServer.contextPath);

        // Add static webjars resource files
        // The maven-dependency-plugin in the pom.xml provides these files.
        WebAppContext res = new WebAppContext(
            server,
            "target/classes/META-INF/resources/webjars",
            EmbeddedJettyServer.contextPath + "/webjars"
        );

        // Create server handler
        HandlerList handlers = new HandlerList();
        handlers.addHandler(res);                   // provides: /plantuml/webjars
        handlers.addHandler(context);               // provides: /plantuml
        handlers.addHandler(new DefaultHandler());  // provides: /

        server.setHandler(handlers);
    }

    public void startServer() throws Exception {
        server.start();
    }

    public void stopServer() throws Exception {
        server.stop();
    }

    public String getServerUrl() {
        return String.format(
            "%s://%s%s",
            server.getURI().getScheme(),
            server.getURI().getAuthority(),
            EmbeddedJettyServer.contextPath
        );
    }

}
