package net.sourceforge.plantuml.servlet;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(TestForm.class);
        suite.addTestSuite(TestImage.class);
        suite.addTestSuite(TestProxy.class);
        //$JUnit-END$
        return suite;
    }

}
