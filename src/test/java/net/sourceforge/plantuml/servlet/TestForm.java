package net.sourceforge.plantuml.servlet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class TestForm extends WebappTestCase {

    /**
     * Verifies that the welcome page has exactly two form with the Bob --> Alice sample
     */
    public void testWelcomePage() throws Exception {
        WebConversation conversation = new WebConversation();
        WebRequest request = new GetMethodWebRequest(getServerUrl());
        WebResponse response = conversation.getResponse(request);
        // Analyze response
        WebForm[] forms = response.getForms();
        assertEquals(2, forms.length);
        assertEquals("url", forms[1].getParameterNames()[0]);
        assertTrue(forms[1].getParameterValue("url").endsWith("/png/" + TestUtils.SEQBOB));
        // Ensure the generated image is present
        assertNotNull(response.getImageWithAltText("PlantUML diagram"));
    }

    /**
     * Verifies that the version image is generated
     */
    public void testVersion() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest(getServerUrl());
        WebResponse response = conversation.getResponse(request);
        WebForm formUMLText = response.getForms()[0];
        formUMLText.setParameter("text", "version");
        response = formUMLText.submit();
        // Analyze response
        WebForm[] forms = response.getForms();
        assertEquals(2, forms.length);
        // Ensure the Text field is correct
        assertEquals("version", forms[0].getParameterValue("text"));
        // Ensure the URL field is correct
        assertTrue(forms[1].getParameterValue("url").endsWith("/png/" + TestUtils.VERSION));
        // Ensure the image is present
        assertNotNull(response.getImageWithAltText("PlantUML diagram"));
    }

    /**
     * Verifies that when the UML text is empty, no image is generated
     */
    public void testEmptyText() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest(getServerUrl());
        WebResponse response = conversation.getResponse(request);
        WebForm formUMLText = response.getForms()[0];
        formUMLText.setParameter("text", "");
        response = formUMLText.submit();
        // Analyze response
        WebForm[] forms = response.getForms();
        assertEquals(2, forms.length);
        // Ensure the Text field is empty
        assertNull(forms[0].getParameterValue("text"));
        // Ensure the URL field is empty
        assertTrue(forms[1].getParameterValue("url").isEmpty());
        // Ensure there is no image
        assertNull(response.getImageWithAltText("PlantUML diagram"));
    }

    /**
     * Verifies that when the encoded URL is empty, no image is generated
     */
    public void testEmptyUrl() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest(getServerUrl());
        WebResponse response = conversation.getResponse(request);
        WebForm formUrl = response.getForms()[1];
        formUrl.setParameter("url", "");
        response = formUrl.submit();
        // Analyze response
        WebForm[] forms = response.getForms();
        assertEquals(2, forms.length);
        // Ensure the Text field is empty
        assertNull(forms[0].getParameterValue("text"));
        // Ensure the URL field is empty
        assertTrue(forms[1].getParameterValue("url").isEmpty());
        // Ensure there is no image
        assertNull(response.getImageWithAltText("PlantUML diagram"));
    }

    /**
     * Verifies that a ditaa diagram is generated
     */
    public void testDitaaText() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest(getServerUrl());
        WebResponse response = conversation.getResponse(request);
        WebForm formDitaaText = response.getForms()[0];
        formDitaaText.setParameter("text", "@startditaa \n*--> \n@endditaa");
        response = formDitaaText.submit();
        // Analyze response
        WebForm[] forms = response.getForms();
        assertEquals(2, forms.length);
        // Ensure the Text field is correct
        assertTrue(forms[0].getParameterValue("text").startsWith("@startditaa"));
        // Ensure the URL field is correct
        assertTrue(forms[1].getParameterValue("url").endsWith("/png/SoWkIImgISaiIKnKuDBIrRLJu798pKi12m00"));
        // Ensure the image is present
        assertNotNull(response.getImageWithAltText("PlantUML diagram"));
    }

    /**
     * Verifies that an image map is produced if the diagram contains a link
     */
    public void testImageMap() throws Exception {
        WebConversation conversation = new WebConversation();
        // Fill the form and submit it
        WebRequest request = new GetMethodWebRequest(getServerUrl());
        WebResponse response = conversation.getResponse(request);
        WebForm formText = response.getForms()[0];
        formText.setParameter("text", "@startuml \nBob -> Alice : [[http://yahoo.com]] Hello \n@enduml");
        response = formText.submit();
        // Analyze response
        // Ensure the generated image is present
        assertNotNull(response.getImageWithAltText("PlantUML diagram"));
        // Ensure the image map is present
        HTMLElement[] maps = response.getElementsByTagName("map");
        assertEquals(1, maps.length);
    }

    /**
     * Verifies that when the encoded source is specified as an URL parameter
     * the diagram is displayed and the source is decoded
     */
    public void testUrlParameter() throws Exception {
        WebConversation conversation = new WebConversation();
        // Submit the request with a url parameter
        WebRequest request = new GetMethodWebRequest(getServerUrl() + "form?url=" + TestUtils.SEQBOB);
        WebResponse response = conversation.getResponse(request);
        // Analyze response
        WebForm[] forms = response.getForms();
        assertEquals(2, forms.length);
        // Ensure the Text field is filled
        assertEquals(forms[0].getParameterValue("text"), "@startuml\nBob -> Alice : hello\n@enduml");
        // Ensure the URL field is filled
        assertEquals(forms[1].getParameterValue("url"), getServerUrl() + "png/" + TestUtils.SEQBOB);
        // Ensure the image is present
        assertNotNull(response.getImageWithAltText("PlantUML diagram"));
    }

}
