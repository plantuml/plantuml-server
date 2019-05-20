package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import java.io.IOException;

public class TestLanguage extends WebappTestCase {

    /**
     * Tests that the language for the current PlantUML server can be obtained through HTTP
     */
    public void testRetrieveLanguage() throws IOException {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "/language");
        WebResponse response = conversation.getResource(request);
        String languageText = response.getText();
        assertTrue("Language contains @startuml", languageText.indexOf("@startuml") > 0);
    }

}
