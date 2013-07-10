package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.*;

/**
 * Utility class for HttpUnit conversations
 */
public class TestUtils {

    /**
     * Try getting a response for the given Conversation and Request show an error message if a 404 error appears
     * 
     * @param conversation The conversation to use
     * @param request
     * @return The response
     * @throws nfe If getting the response fails
     */
    public static WebResponse tryGetResponse(WebConversation conversation, WebRequest request) throws Exception {
        WebResponse response = null;
        try {
            response = conversation.getResponse(request);
        } catch (HttpNotFoundException nfe) {
            System.err.println("The URL '" + request.getURL() + "' is no more active");
            throw nfe;
        }
        return response;
    }

}
