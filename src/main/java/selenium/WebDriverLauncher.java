package selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;

public class WebDriverLauncher {
    private String url;
    private static WebDriver driver = createWebDriver();
    private static final String driversDir = "\\assets\\drivers\\";

    public WebDriverLauncher(String url) {
        this.url = url;
        driver.get(url);
    }

    public static WebDriver createWebDriver() {
        WebDriver driver = null;
        String chromeDriverPath = null;
        try {
            chromeDriverPath = new File(".").getCanonicalPath() + driversDir + "chromedriver";
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //Пока потестим с хромом. Можно в любой момент заменить.
            System.setProperty("webdriver.chrome.driver", chromeDriverPath + ".exe");
            driver = new ChromeDriver();
        } catch (IllegalStateException ise) {
            try {
                //System.setProperty("webdriver.chrome.driver", chromeDriverPath + "1.exe");
                //driver = new ChromeDriver();
            } catch (IllegalStateException ise2) {
                //System.setProperty("webdriver.chrome.driver", chromeDriverPath + "2.exe");
                //driver = new ChromeDriver();
            }
        }
        return driver;
    }

    public static void main(String[] args) {
        WebDriverLauncher webDriverLauncher = new WebDriverLauncher("https://habr.com");
    }

}
