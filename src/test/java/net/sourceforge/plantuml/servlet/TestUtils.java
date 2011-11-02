package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.*;

/**
 * Utility class for HttpUnit conversations
 */
public class TestUtils {

    /**
     * Return the URL of the PlantUMLServlet, deployed on the testing web server
     * in the following form http://server/contextroot/
     * Note the trailing slash (/)
     * @return the URL
     */
     public static String getServerUrl() {
        return "http://localhost/plantuml/";
    }

     /**
     * Try getting a response for the given Conversation and Request
     * show an error message if a 404 error appears
     * @param conversation - the conversation to use
     * @param request
     * @return the response
     * @throws an Exception if getting the response fails
     */
     public static WebResponse tryGetResponse(WebConversation conversation, WebRequest request) throws Exception {
        WebResponse response=null;
        try {
            response = conversation.getResponse( request );
        } catch (HttpNotFoundException nfe) {
            System.err.println("The URL '"+request.getURL()+"' is not active any more");
            throw nfe;
        }
        return response;
    }

}

