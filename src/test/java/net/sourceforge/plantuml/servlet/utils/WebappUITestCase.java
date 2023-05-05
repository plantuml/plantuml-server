package net.sourceforge.plantuml.servlet.utils;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public abstract class WebappUITestCase extends WebappTestCase {

    public WebDriver driver;
    public JavascriptExecutor js;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        driver = JUnitWebDriver.getDriver();
        js = (JavascriptExecutor)driver;
    }

    @AfterEach
    public void tearDown() throws Exception {
        driver.close();
        super.tearDown();
    }

    public boolean waitUntilJavascriptIsLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return js.executeScript("return document.readyState").toString().equals("complete");
            }
        });
    }

    public boolean waitUntilEditorIsLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return js.executeScript("return document.editor === undefined").toString().equals("false");
            }
        });
    }

    public boolean waitUntilAutoRefreshCompleted() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return js.executeScript("return document.appConfig.autoRefreshState").toString().equals("complete");
            }
        });
    }

    public boolean waitUntilUIIsLoaded() {
        return waitUntilEditorIsLoaded();
    }

    public String getEditorValue() {
        return (String)js.executeScript("return document.editor.getValue();");
    }

    public void setEditorValue(String code) {
        js.executeScript("return document.editor.getModel().setValue(`" + code.replace("`", "\\`") + "`);");
    }

    public String getURLValue() {
        return driver.findElement(By.id("url")).getAttribute("value");
    }

    public Dimension getImageSize() {
        WebElement img = driver.findElement(By.id("diagram-png"));
        return new Dimension(
            Integer.parseInt(img.getAttribute("width")),
            Integer.parseInt(img.getAttribute("height"))
        );
        // return driver.findElement(By.id("diagram-png")).getSize();
    }

    public WebElement getImageMap() {
        return driver.findElement(By.id("plantuml_map"));
    }
}
