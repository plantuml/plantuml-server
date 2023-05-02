package net.sourceforge.plantuml.servlet;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        // $JUnit-BEGIN$
        suite.addTestSuite(TestWebUI.class);
        suite.addTestSuite(TestImage.class);
        suite.addTestSuite(TestAsciiArt.class);
        suite.addTestSuite(TestSVG.class);
        suite.addTestSuite(TestMap.class);
        suite.addTestSuite(TestCharset.class);
        // $JUnit-END$
        return suite;
    }

}
