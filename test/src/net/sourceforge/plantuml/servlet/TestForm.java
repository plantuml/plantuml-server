package net.sourceforge.plantuml.servlet;

import junit.framework.*;
import com.meterware.httpunit.*;

public class TestForm extends TestCase {
	
	/**
	 * Verifies that the welcome page has exactly two form
	 * with the Bob --> Alice sample
	 */
    public void testWelcomePage() throws Exception {
        WebConversation     conversation = new WebConversation();
	    WebRequest request = new GetMethodWebRequest( "http://localhost/plantuml/" );
	    WebResponse response = tryGetResponse(conversation, request );
	      
	    WebForm forms[] = response.getForms();
	    assertEquals( 2, forms.length );
        assertEquals( "url", forms[1].getParameterNames()[0] );
        FormControl urlInput = forms[1].getParameter("url").getControl();
        assertEquals( "http://localhost:80/plantuml/img/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000", 
                      forms[1].getParameterValue("url"));
        assertEquals( "INPUT", urlInput.getTagName());
        // Ensure the generated image is present
        assertEquals( 1, response.getImages().length);

	}

    /**
     * Verifies that when the UML text is empty, no image is generated
     */
    public void testEmptyText() throws Exception {
        WebConversation     conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest( "http://localhost/plantuml/form" );
        WebResponse response = tryGetResponse(conversation, request );
        WebForm formUMLText = response.getForms()[0];
        formUMLText.setParameter("text", "");
        response = tryGetResponse( conversation, formUMLText.getRequest());
        // Analyze response
        WebForm forms[] = response.getForms();
        assertEquals( 2, forms.length );
        // Ensure the Text field is empty
        assertNull( forms[0].getParameterValue("text"));
        // Ensure the URL field is empty
        assertTrue( forms[1].getParameterValue("url").isEmpty());
        // Ensure there is no image
        assertEquals( 0, response.getImages().length);
    }
        
    /**
     * try getting a response for the given Conversation and Request
     * show an error message if a 404 error appears
     * @param conversation - the conversation to use
     * @param request
     * @return the response
     * @throws an Exception if getting the response fails
     */
    public WebResponse tryGetResponse(WebConversation conversation, WebRequest request) throws Exception {
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
