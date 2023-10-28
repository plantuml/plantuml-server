package net.sourceforge.plantuml.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import net.sourceforge.plantuml.servlet.utils.TestUtils;
import net.sourceforge.plantuml.servlet.utils.WebappUITestCase;


public class TestWebUI extends WebappUITestCase {

    /**
     * Verifies that the welcome page has exactly two form with the Bob --> Alice sample
     */
    @Test
    public void testWelcomePage() {
        driver.get(getServerUrl());
        Assertions.assertTrue(waitUntilUIIsLoaded(), "UI loading completed");
        // ensure the editor text is correct
        String text = getEditorValue();
        Assertions.assertEquals(TestUtils.SEQBOBCODE, text);
        // ensure the URL field is correct
        String url = getURLValue();
        Assertions.assertTrue(url.endsWith("/png/" + TestUtils.SEQBOB));
        // ensure the generated image is present
        Dimension dim = getImageSize();
        Assertions.assertNotEquals(0, dim.getHeight());  // 145
        Assertions.assertNotEquals(0, dim.getWidth());   // 134
    }

    /**
     * Verifies that the version image is generated
     */
    @Test
    public void testVersion() {
        driver.get(getServerUrl());
        Assertions.assertTrue(waitUntilUIIsLoaded(), "UI loading completed");
        // change code and observe result
        setEditorValue(TestUtils.VERSIONCODE);
        Assertions.assertTrue(waitUntilAutoRefreshCompleted(), "Auto update done");
        // ensure the editor text is correct
        String text = getEditorValue();
        Assertions.assertEquals(TestUtils.VERSIONCODE, text);
        // ensure the URL field is correct
        String url = getURLValue();
        Assertions.assertTrue(url.endsWith("/png/" + TestUtils.VERSION));
        // ensure the generated image is present
        Dimension dim = getImageSize();
        Assertions.assertNotEquals(0, dim.getHeight());  // 242
        Assertions.assertNotEquals(0, dim.getWidth());   // 472
    }

    // /**
    //  * Verifies that when the UML text is empty, ...
    //  * old behavior: default page and image is generated
    //  */
    // @Test
    // public void testEmptyText() {
    //     // ...
    // }

    // /**
    //  * Verifies that when the encoded URL is empty, ...
    //  * old behavior: default page and image is generated
    //  */
    // @Test
    // public void testEmptyUrl() {
    //     // ...
    // }

    /**
     * Verifies that a ditaa diagram is generated
     */
    @Test
    public void testDitaaText() {
        driver.get(getServerUrl());
        Assertions.assertTrue(waitUntilUIIsLoaded(), "UI loading completed");
        // change code and observe result
        setEditorValue("@startditaa \n*--> \n@endditaa");
        Assertions.assertTrue(waitUntilAutoRefreshCompleted(), "Auto update done");
        // ensure the editor text is correct
        String text = getEditorValue();
        Assertions.assertEquals("@startditaa \n*--> \n@endditaa", text);
        // ensure the URL field is correct
        String url = getURLValue();
        Assertions.assertTrue(url.endsWith("/png/SoWkIImgISaiIKnKuDBIrRLJu798pKi12m00"));
        // ensure the generated image is present
        Dimension dim = getImageSize();
        Assertions.assertNotEquals(0, dim.getHeight());
        Assertions.assertNotEquals(0, dim.getWidth());
    }

    /**
     * Verifies that an image map is produced if the diagram contains a link
     */
    @Test
    public void testImageMap() {
        driver.get(getServerUrl());
        Assertions.assertTrue(waitUntilUIIsLoaded(), "UI loading completed");
        // change code and observe result
        setEditorValue("@startuml\nBob -> Alice : [[http://yahoo.com]] Hello\n@enduml");
        Assertions.assertTrue(waitUntilAutoRefreshCompleted(), "Auto update done");
        // ensure the editor text is correct
        String text = getEditorValue();
        Assertions.assertEquals("@startuml\nBob -> Alice : [[http://yahoo.com]] Hello\n@enduml", text);
        // ensure the URL field is correct
        String url = getURLValue();
        Assertions.assertTrue(url.endsWith("/png/SyfFKj2rKt3CoKnELR1IY8xEA2afiDBNhqpCoC_NIyxFZOrLy4ZDoSa70000"));
        // ensure the generated image is present
        Dimension dim = getImageSize();
        Assertions.assertNotEquals(0, dim.getHeight());
        Assertions.assertNotEquals(0, dim.getWidth());
        // ensure the image map is present
        WebElement map = getImageMap();
        Assertions.assertNotNull(map);
        Assertions.assertEquals(1, Integer.parseInt(map.getAttribute("childElementCount")));
        // ensure the map button is visible
        WebElement btnMap = driver.findElement(By.id("map-diagram-link"));
        Assertions.assertTrue(btnMap.isDisplayed());
    }

    /**
     * Verifies that when the encoded source is specified as an URL parameter
     * the diagram is displayed and the source is decoded
     */
    @Test
    public void testUrlParameter() {
        driver.get(getServerUrl() + "/form?url=" + TestUtils.SEQBOB);
        Assertions.assertTrue(waitUntilUIIsLoaded(), "UI loading completed");
        // ensure the editor text is correct
        String text = getEditorValue();
        Assertions.assertEquals(TestUtils.SEQBOBCODE, text);
        // ensure the URL field is correct
        String url = getURLValue();
        Assertions.assertTrue(url.endsWith("/png/" + TestUtils.SEQBOB));
        // ensure the generated image is present
        Dimension dim = getImageSize();
        Assertions.assertNotEquals(0, dim.getHeight());
        Assertions.assertNotEquals(0, dim.getWidth());
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
    @Test
    public void testIndexPage() {
        driver.get(
            getServerUrl() + "/uml/1/" +
            "SyfFKj2rKt3CoKnELR1Io4ZDoSddoaijBqXCJ-Lo0ahQwA99Eg7go4ajKIzMA4dCoKPNdfHQKf9Qf92NNuAknqQjA34ppquXgJ8Lbrr0AG00"
        );
        Assertions.assertTrue(waitUntilUIIsLoaded(), "UI loading completed");
        // ensure the editor text is correct
        String text = getEditorValue();
        Assertions.assertEquals(
            "@startuml\nBob -> Alice : hello\nnewpage\nBob <- Alice : hello\nBob -> Alice : let's talk\nBob <- Alice : better not\nBob -> Alice : <&rain> bye\nnewpage\nBob <- Alice : bye\n@enduml",
            text
        );
        // ensure the URL field is correct
        String url = getURLValue();
        Assertions.assertTrue(url.endsWith("/png/1/SyfFKj2rKt3CoKnELR1Io4ZDoSddoaijBqXCJ-Lo0ahQwA99Eg7go4ajKIzMA4dCoKPNdfHQKf9Qf92NNuAknqQjA34ppquXgJ8Lbrr0AG00"));
        // ensure the generated image is present
        Dimension dim = getImageSize();
        Assertions.assertNotEquals(0, dim.getHeight());
        Assertions.assertNotEquals(0, dim.getWidth());
        // ensure the correct index was generated
        Assertions.assertTrue(dim.getHeight() > 200);  // 222
        Assertions.assertTrue(dim.getHeight() < 250);  // 222
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
    @Test
    public void testIndexPageWithNoDefinedIndex() {
        driver.get(
            getServerUrl() + "/uml/" +
            "SyfFKj2rKt3CoKnELR1Io4ZDoSddoaijBqXCJ-Lo0ahQwA99Eg7go4ajKIzMA4dCoKPNdfHQKf9Qf92NNuAknqQjA34ppquXgJ8Lbrr0AG00"
        );
        Assertions.assertTrue(waitUntilUIIsLoaded(), "UI loading completed");
        // ensure the editor text is correct
        String text = getEditorValue();
        Assertions.assertEquals(
            "@startuml\nBob -> Alice : hello\nnewpage\nBob <- Alice : hello\nBob -> Alice : let's talk\nBob <- Alice : better not\nBob -> Alice : <&rain> bye\nnewpage\nBob <- Alice : bye\n@enduml",
            text
        );
        // ensure the URL field is correct
        String url = getURLValue();
        Assertions.assertTrue(url.endsWith("/png/SyfFKj2rKt3CoKnELR1Io4ZDoSddoaijBqXCJ-Lo0ahQwA99Eg7go4ajKIzMA4dCoKPNdfHQKf9Qf92NNuAknqQjA34ppquXgJ8Lbrr0AG00"));
        // ensure the generated image is present
        Dimension dim = getImageSize();
        Assertions.assertNotEquals(0, dim.getHeight());
        Assertions.assertNotEquals(0, dim.getWidth());
        // ensure the correct index was generated
        Assertions.assertTrue(dim.getHeight() > 100);  // 132
        Assertions.assertTrue(dim.getHeight() < 150);  // 132
    }
}
