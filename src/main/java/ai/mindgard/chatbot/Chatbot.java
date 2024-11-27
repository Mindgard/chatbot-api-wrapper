package ai.mindgard.chatbot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Chatbot {

    public static final String JS_ADD_TEXT_TO_INPUT =
    """
            var elm = arguments[0], txt = arguments[1];
            elm.value += txt;
            console.log(txt);
            elm.dispatchEvent(new Event('change'));
            """;

    private static final ChromeOptions DEFAULT_CHROME_OPTIONS = new ChromeOptions();

    private final RemoteWebDriver browser;
    private Runnable pause;
    private final ChatbotConfig config;
    private int id;

    public Chatbot(ChatbotConfig config, int id) {
        this(config, id, new ChromeDriver(DEFAULT_CHROME_OPTIONS), Chatbot::sleep);
    }

    public Chatbot(ChatbotConfig config, int id, RemoteWebDriver driver, Runnable pause) {
        this.config = config;
        this.id = id;
        this.browser = driver;
        this.pause = pause;
        browser.get(config.url());
        pause.run();
        var ready = elementAt(config.readySelector());
        ready.click();
    }

    private WebElement elementAt(String selector) {
        var wait = new WebDriverWait(browser, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return browser.findElement(By.cssSelector(selector));
    }

    public String send(String message) {
        System.out.println("[" + id + "] -> " + message);
        var input = elementAt(config.inputSelector());
        browser.executeScript(JS_ADD_TEXT_TO_INPUT, input, message);
        input.sendKeys(" ");
        pause.run();
        elementAt(config.submitSelector()).click();

        elementAt(config.inputSelector());

        elementAt(config.outputSelector());

        elementAt(config.submitSelector());

        var replies = browser.findElements(By.cssSelector(config.outputSelector()));

        String response = replies.get(replies.size() - 1).getText();
        System.out.println("[" + id + "] <- " + response);
        return response;
    }

    private static void sleep() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}