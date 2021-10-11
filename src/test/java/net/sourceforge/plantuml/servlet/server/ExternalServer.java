package net.sourceforge.plantuml.servlet.server;


public class ExternalServer implements ServerUtils {

    private final String uri;

    public ExternalServer(String uri) {
        this.uri = uri;
    }

    @Override
    public void startServer() throws Exception { }

    @Override
    public void stopServer() throws Exception { }

    @Override
    public String getServerUrl() {
        return this.uri;
    }
    
}
