package selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import util.HTML;
import util.Link;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WebDriverLauncher {
    private String url;
    private static WebDriver driver = createWebDriver();
    private static final String driversDir = "\\src\\main\\resources\\drivers\\";
    private HTML currentSource;
    public WebDriverLauncher(String url) {
       getWebsite(url);
    }

    public WebDriverLauncher(Link link) {
        getWebsite(link.toString());
    }

    public WebDriverLauncher getNextWebsite(String url) {
        this.url = url;
        driver.get(url);
        return this;
    }

    public WebDriverLauncher getNextWebsite(Link link) {
        this.url = link.toString();
        driver.get(url);
        currentSource = getHTMLSource();
        return this;
    }

    public WebDriverLauncher () {
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private void getWebsite(String url) {
        this.url = url;
        driver.get(url);
    }

    public String getStringSource(){
        return driver.getPageSource();
    }

    public HTML getHTMLSource(){
       return new HTML(driver.getPageSource(), new Link(url));
    }

    public HTML clickAllElementsToGetNewSource(String tagName){
        clickSomeElements(tagName);
        HTML newSource = getHTMLSource();
        if (newSource.size() > currentSource.size()) {
            return getHTMLSource();
        } else {
            return HTML.getEmptySource();
        }
    }

    private WebElement clickFirstInteractableElement(String tagName){
        List<WebElement> elements = driver.findElements(By.tagName(tagName));
        for (WebElement element : elements) {
            try {
                element.click();
            } catch (ElementNotInteractableException enie) {
                continue;
            }
            return element;
        }
        return null;
    }

    public void clickSomeElements(String tagName){
        List<WebElement> elements = driver.findElements(By.tagName(tagName));
        for (WebElement element : elements) {
            String text = element.getText();
            System.out.println(text);
            try {
                element.click();
                element.submit();
            } catch (ElementNotInteractableException enie) {

            }
        }
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
            System.out.println("Trying to set first driver...");
            System.setProperty("webdriver.chrome.driver", chromeDriverPath + ".exe");
            driver = new ChromeDriver();
        } catch (IllegalStateException | SessionNotCreatedException ise) {
            ise.printStackTrace();
            System.out.println("First driver '"+ chromeDriverPath +".exe' is shit. Trying to set second driver...");
            try {
                System.setProperty("webdriver.chrome.driver", chromeDriverPath + "1.exe");
                driver = new ChromeDriver();
            } catch (IllegalStateException | SessionNotCreatedException ise2) {
                ise2.printStackTrace();
                System.err.println("Second driver '"+chromeDriverPath+"1.exe' is shit too");
                //System.setProperty("webdriver.chrome.driver", chromeDriverPath + "2.exe");
                //driver = new ChromeDriver();
            }
        }
        return driver;
    }

}
