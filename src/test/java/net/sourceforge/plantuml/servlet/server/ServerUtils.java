package net.sourceforge.plantuml.servlet.server;


public interface ServerUtils {

    public void startServer() throws Exception;

    public void stopServer() throws Exception;

    public abstract String getServerUrl();

}
