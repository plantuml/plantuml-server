package net.sourceforge.plantuml.servlet;

import junit.framework.TestCase;
import com.meterware.httpunit.*;

public class TestForm extends TestCase {
	
	/**
	 * Verifies that the welcome page has exactly two form
	 * with the Bob --> Alice sample
	 */
    public void testWelcomePage() throws Exception {
        WebConversation conversation = new WebConversation();
	    WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl());
	    WebResponse response = TestUtils.tryGetResponse(conversation, request );
        // Analyze response  
	    WebForm forms[] = response.getForms();
	    assertEquals( 2, forms.length );
        assertEquals( "url", forms[1].getParameterNames()[0] );
        assertTrue( forms[1].getParameterValue("url").endsWith("/img/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000"));
        // Ensure the generated image is present
        assertEquals( 1, response.getImages().length);

	}

    /**
     * Verifies that the version image is generated
     */
    public void testVersion() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl());
        WebResponse response = TestUtils.tryGetResponse(conversation, request );
        WebForm formUMLText = response.getForms()[0];
        formUMLText.setParameter("text", "version");
        response = formUMLText.submit();
        // Analyze response
        WebForm forms[] = response.getForms();
        assertEquals( 2, forms.length );
        // Ensure the Text field is correct
        assertEquals( "version", forms[0].getParameterValue("text"));
        // Ensure the URL field is correct
        assertTrue( forms[1].getParameterValue("url").endsWith("/img/AqijAixCpmC0"));
        // Ensure the image is present
        assertEquals( 1, response.getImages().length);
    }

    /**
     * Verifies that when the UML text is empty, no image is generated
     */
    public void testEmptyText() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl());
        WebResponse response = TestUtils.tryGetResponse(conversation, request );
        WebForm formUMLText = response.getForms()[0];
        formUMLText.setParameter("text", "");
        response = formUMLText.submit();
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
     * Verifies that when the encoded URL is empty, no image is generated
     */
    public void testEmptyUrl() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl() );
        WebResponse response = TestUtils.tryGetResponse(conversation, request );
        WebForm formUrl = response.getForms()[1];
        formUrl.setParameter("url", "");
        response = formUrl.submit();
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
     * Verifies that a ditaa diagram is generated
     */
    public void testDitaaText() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest( TestUtils.getServerUrl() );
        WebResponse response = TestUtils.tryGetResponse(conversation, request );
        WebForm formDitaaText = response.getForms()[0];
        formDitaaText.setParameter("text", "@startditaa \n*--> \n@endditaa");
        response = formDitaaText.submit();
        // Analyze response
        WebForm forms[] = response.getForms();
        assertEquals( 2, forms.length );
        // Ensure the Text field is correct
        assertTrue( forms[0].getParameterValue("text").startsWith( "@startditaa"));
        // Ensure the URL field is correct
        assertTrue( forms[1].getParameterValue("url").endsWith("/img/SoWkIImgISaiIKnKuDBIrRLJu798pKi12m00"));
        // Ensure the image is present
        assertEquals( 1, response.getImages().length);
    }

}
