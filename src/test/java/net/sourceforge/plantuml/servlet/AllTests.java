package net.sourceforge.plantuml.servlet;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        // $JUnit-BEGIN$
        suite.addTestSuite(TestAsciiArt.class);
        suite.addTestSuite(TestAsciiCoder.class);
        suite.addTestSuite(TestCharset.class);
        suite.addTestSuite(TestCheck.class);
        suite.addTestSuite(TestEPS.class);
        suite.addTestSuite(TestImage.class);
        suite.addTestSuite(TestLanguage.class);
        suite.addTestSuite(TestMap.class);
        suite.addTestSuite(TestMultipageUml.class);
        suite.addTestSuite(TestOldProxy.class);
        suite.addTestSuite(TestProxy.class);
        suite.addTestSuite(TestSVG.class);
        suite.addTestSuite(TestWebUI.class);
        // $JUnit-END$
        return suite;
    }

}
