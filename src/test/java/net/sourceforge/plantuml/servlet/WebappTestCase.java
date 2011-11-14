package net.sourceforge.plantuml.servlet;

import junit.framework.TestCase;

public abstract class WebappTestCase extends TestCase {

    private ServerUtils serverUtils;

    public WebappTestCase() {
        super();
    }

    public WebappTestCase(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        serverUtils = new ServerUtils(true);
    }

    @Override
    public void tearDown() throws Exception {
        serverUtils.stopServer();
    }

    protected String getServerUrl() {
        return serverUtils.getServerUrl();
    }

}
