package net.sourceforge.plantuml.servlet.server;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


public class EmbeddedJettyServer implements ServerUtils {

    private Server server;

    public EmbeddedJettyServer() {
        server = new Server(new InetSocketAddress("127.0.0.1", 0));
        server.addBean(new WebAppContext(server, "src/main/webapp", "/plantuml"));
    }

    public void startServer() throws Exception {
        server.start();
    }

    public void stopServer() throws Exception {
        server.stop();
    }

    public String getServerUrl() {
        Connector connector = server.getConnectors()[0];
        return String.format("http://%s:%d/plantuml", connector.getHost(), connector.getLocalPort());
    }

}
