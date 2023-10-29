package net.sourceforge.plantuml.servlet.utils;

import java.time.Duration;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;


public abstract class JUnitWebDriver {

    public static final String browser;

    static {
        browser = System.getProperty("system.test.browser", "firefox");
    }

    public static WebDriver getDriver() {
        WebDriver driver;
        switch (browser.toLowerCase()) {
            case "chrome":
                driver = getChromeDriver();
                break;
            case "edge":
                driver = SystemUtils.IS_OS_WINDOWS ? getEdgeDriver() : getChromiumDriver();
                break;
            case "firefox":
                driver = getFirefoxDriver();
                break;
            default:
                driver = getChromiumDriver();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(1024, 768));
        return driver;
    }

    private static WebDriver getChromiumDriver() {
        WebDriverManager.chromiumdriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-gpu");
        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        return new ChromeDriver(options);
    }

    private static WebDriver getChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-gpu");
        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        return new ChromeDriver(options);
    }

    private static WebDriver getFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        return new FirefoxDriver(options);
    }

    private static WebDriver getEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        options.addArguments("headless");
        return new EdgeDriver(options);
    }
}
