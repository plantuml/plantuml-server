package net.sourceforge.plantuml.servlet;

import java.io.IOException;
import java.net.URL;

import net.sourceforge.plantuml.servlet.utils.WebappTestCase;


public class TestLanguage extends WebappTestCase {

    /**
     * Tests that the language for the current PlantUML server can be obtained through HTTP
     */
    public void testRetrieveLanguage() throws IOException {
        final URL url = new URL(getServerUrl() + "/language");
        String languageText = getContentText(url);
        assertTrue("Language contains @startuml", languageText.indexOf("@startuml") > 0);
    }

}
