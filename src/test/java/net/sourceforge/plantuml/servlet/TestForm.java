package net.sourceforge.plantuml.servlet;

import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;


public class TestForm extends WebappTestCase {

    /**
     * Verifies that the welcome page has exactly two form with the Bob --> Alice sample
     */
    public void testWelcomePage() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(getServerUrl());
            // Analyze response
            List<HtmlForm> forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals("@startuml\nBob -> Alice : hello\n@enduml", text);
            // Ensure the URL field is correct
            HtmlInput url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/" + TestUtils.SEQBOB));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            assertNotEquals(0, img.getImageReader().getHeight(0));  // 131
            assertNotEquals(0, img.getImageReader().getWidth(0));   // 120
        }
    }

    /**
     * Verifies that the version image is generated
     */
    public void testVersion() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(getServerUrl());
            page.initialize();
            // Fill the form and submit it
            page.executeJavaScript("document.myCodeMirror.setValue('version')");
            HtmlForm form = page.getForms().get(0);
            HtmlSubmitInput btn = form.getFirstByXPath("//input[contains(@type, 'submit')]");
            page = btn.click();
            // Analyze response
            List<HtmlForm> forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals("@startuml\nversion\n@enduml", text);
            // Ensure the URL field is correct
            HtmlInput url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/" + TestUtils.VERSION));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            assertNotEquals(0, img.getImageReader().getHeight(0));  // 186
            assertNotEquals(0, img.getImageReader().getWidth(0));   // 519
        }
    }

    /**
     * Verifies that when the UML text is empty, default page and image is generated
     */
    public void testEmptyText() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(getServerUrl());
            page.initialize();
            // Fill the form and submit it
            page.executeJavaScript("document.myCodeMirror.setValue('')");
            HtmlForm form = page.getForms().get(0);
            HtmlSubmitInput btn = form.getFirstByXPath("//input[contains(@type, 'submit')]");
            page = btn.click();
            // Analyze response
            List<HtmlForm> forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals("@startuml\nBob -> Alice : hello\n@enduml", text);
            // Ensure the URL field is correct
            HtmlInput url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/" + TestUtils.SEQBOB));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            assertNotEquals(0, img.getImageReader().getHeight(0));  // 131
            assertNotEquals(0, img.getImageReader().getWidth(0));   // 120
        }
    }

    /**
     * Verifies that when the encoded URL is empty, default page and image is generated
     */
    public void testEmptyUrl() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(getServerUrl());
            page.initialize();
            // Fill the form and submit it
            List<HtmlForm> forms = page.getForms();
            HtmlInput url = forms.get(1).getInputByName("url");
            url.setAttribute("value", "");
            HtmlSubmitInput btn = forms.get(1).getFirstByXPath("//input[contains(@type, 'submit')]");
            page = btn.click();
            // Analyze response
            forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals("@startuml\nBob -> Alice : hello\n@enduml", text);
            // Ensure the URL field is correct
            url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/" + TestUtils.SEQBOB));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            assertNotEquals(0, img.getImageReader().getHeight(0));  // 131
            assertNotEquals(0, img.getImageReader().getWidth(0));   // 120
        }
    }

    /**
     * Verifies that a ditaa diagram is generated
     */
    public void testDitaaText() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(getServerUrl());
            page.initialize();
            // Fill the form and submit it
            page.executeJavaScript("document.myCodeMirror.setValue(`@startditaa \n*--> \n@endditaa`)");
            HtmlForm form = page.getForms().get(0);
            HtmlSubmitInput btn = form.getFirstByXPath("//input[contains(@type, 'submit')]");
            page = btn.click();
            // Analyze response
            List<HtmlForm> forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals("@startditaa \n*--> \n@endditaa", text);
            // Ensure the URL field is correct
            HtmlInput url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/SoWkIImgISaiIKnKuDBIrRLJu798pKi12m00"));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            assertNotEquals(0, img.getImageReader().getHeight(0));  // 70
            assertNotEquals(0, img.getImageReader().getWidth(0));   // 90
        }
    }

    /**
     * Verifies that an image map is produced if the diagram contains a link
     */
    public void testImageMap() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(getServerUrl());
            page.initialize();
            // Fill the form and submit it
            page.executeJavaScript("document.myCodeMirror.setValue(`@startuml\nBob -> Alice : [[http://yahoo.com]] Hello\n@enduml`)");
            HtmlForm form = page.getForms().get(0);
            HtmlSubmitInput btn = form.getFirstByXPath("//input[contains(@type, 'submit')]");
            page = btn.click();
            // Analyze response
            List<HtmlForm> forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals("@startuml\nBob -> Alice : [[http://yahoo.com]] Hello\n@enduml", text);
            // Ensure the URL field is correct
            HtmlInput url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/SyfFKj2rKt3CoKnELR1IY8xEA2afiDBNhqpCoC_NIyxFZOrLy4ZDoSa70000"));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            assertNotEquals(0, img.getImageReader().getHeight(0));  // 131
            assertNotEquals(0, img.getImageReader().getWidth(0));   // 231
            // Ensure the image map is present
            DomElement map = page.getElementById("plantuml_map");
            assertNotNull(map);
            assertEquals(1, map.getChildElementCount());
        }
    }

    /**
     * Verifies that when the encoded source is specified as an URL parameter
     * the diagram is displayed and the source is decoded
     */
    public void testUrlParameter() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            // Submit the request with a url parameter
            HtmlPage page = webClient.getPage(getServerUrl() + "/form?url=" + TestUtils.SEQBOB);
            page.initialize();
            // Analyze response
            List<HtmlForm> forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals("@startuml\nBob -> Alice : hello\n@enduml", text);
            // Ensure the URL field is correct
            HtmlInput url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/" + TestUtils.SEQBOB));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            assertNotEquals(0, img.getImageReader().getHeight(0));  // 131
            assertNotEquals(0, img.getImageReader().getWidth(0));   // 120
        }
    }

    /**
     * Verifies that an multipage diagram renders correct given index.
     *
     * Bob -> Alice : hello
     * newpage
     * Bob <- Alice : hello
     * Bob -> Alice : let's talk
     * Bob <- Alice : better not
     * Bob -> Alice : <&rain> bye
     * newpage
     * Bob <- Alice : bye
     */
    public void testIndexPage() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(
                getServerUrl() + "/uml/1/" +
                "SyfFKj2rKt3CoKnELR1Io4ZDoSddoaijBqXCJ-Lo0ahQwA99Eg7go4ajKIzMA4dCoKPNdfHQKf9Qf92NNuAknqQjA34ppquXgJ8Lbrr0AG00"
            );
            // Analyze response
            List<HtmlForm> forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals(
                "@startuml\nBob -> Alice : hello\nnewpage\nBob <- Alice : hello\nBob -> Alice : let's talk\nBob <- Alice : better not\nBob -> Alice : <&rain> bye\nnewpage\nBob <- Alice : bye\n@enduml",
                text
            );
            // Ensure the URL field is correct
            HtmlInput url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/1/SyfFKj2rKt3CoKnELR1Io4ZDoSddoaijBqXCJ-Lo0ahQwA99Eg7go4ajKIzMA4dCoKPNdfHQKf9Qf92NNuAknqQjA34ppquXgJ8Lbrr0AG00"));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            int height = img.getImageReader().getHeight(0);
            assertNotEquals(0, height);  // 222
            assertNotEquals(0, img.getImageReader().getWidth(0));  // 152
            // Ensure the correct index was generated
            assertTrue(height > 200);  // 222
            assertTrue(height < 250);  // 222
        }
    }

    /**
     * Verifies that an multipage diagram renders correct even if no index is specified.
     *
     * Bob -> Alice : hello
     * newpage
     * Bob <- Alice : hello
     * Bob -> Alice : let's talk
     * Bob <- Alice : better not
     * Bob -> Alice : <&rain> bye
     * newpage
     * Bob <- Alice : bye
     */
    public void testIndexPageWithNoDefinedIndex() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(
                getServerUrl() + "/uml/" +
                "SyfFKj2rKt3CoKnELR1Io4ZDoSddoaijBqXCJ-Lo0ahQwA99Eg7go4ajKIzMA4dCoKPNdfHQKf9Qf92NNuAknqQjA34ppquXgJ8Lbrr0AG00"
            );
            // Analyze response
            List<HtmlForm> forms = page.getForms();
            assertEquals(2, forms.size());
            // Ensure the Text field is correct
            String text = ((HtmlTextArea)(forms.get(0).getFirstByXPath("//textarea[contains(@name, 'text')]"))).getTextContent();
            assertEquals(
                "@startuml\nBob -> Alice : hello\nnewpage\nBob <- Alice : hello\nBob -> Alice : let's talk\nBob <- Alice : better not\nBob -> Alice : <&rain> bye\nnewpage\nBob <- Alice : bye\n@enduml",
                text
            );
            // Ensure the URL field is correct
            HtmlInput url = forms.get(1).getInputByName("url");
            assertNotNull(url);
            assertTrue(url.getAttribute("value").endsWith("/png/SyfFKj2rKt3CoKnELR1Io4ZDoSddoaijBqXCJ-Lo0ahQwA99Eg7go4ajKIzMA4dCoKPNdfHQKf9Qf92NNuAknqQjA34ppquXgJ8Lbrr0AG00"));
            // Ensure the generated image is present
            HtmlImage img = page.getFirstByXPath("//img[contains(@alt, 'PlantUML diagram')]");
            int height = img.getImageReader().getHeight(0);
            assertNotEquals(0, height);  // 132
            assertNotEquals(0, img.getImageReader().getWidth(0));  // 152
            // Ensure the correct index was generated
            assertTrue(height > 100);  // 132
            assertTrue(height < 150);  // 132
        }
    }

}
