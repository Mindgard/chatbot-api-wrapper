package ai.mindgard.chatbot;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

import static ai.mindgard.chatbot.Chatbot.JS_ADD_TEXT_TO_INPUT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ChatbotTest {
    ChatbotConfig config = new ChatbotConfig("https://example.com/chatbot", ".ready_selector", ".input_selector", ".submit_selector", ".output_selector");
    RemoteWebDriver browser = mock(RemoteWebDriver.class, RETURNS_DEEP_STUBS);


    @Test public void chatbot_opens_browser() {
        when(browser.findElement(By.cssSelector(config.readySelector())).isDisplayed()).thenReturn(true);
        new Chatbot(config, 5, browser, () -> {});
        verify(browser).get(config.url());
    }

    @Test
    public void sends_messages_to_chatbot_and_returns_response() {
        var readySelector = browser.findElement(By.cssSelector(config.readySelector()));
        var inputSelector = browser.findElement(By.cssSelector(config.inputSelector()));
        var submitSelector = browser.findElement(By.cssSelector(config.submitSelector()));
        var outputSelector = browser.findElement(By.cssSelector(config.outputSelector()));

        when(readySelector.isDisplayed()).thenReturn(true);

        var chatbot = new Chatbot(config, 5, browser, () -> {});

        when(outputSelector.isDisplayed()).thenReturn(true);
        when(inputSelector.isDisplayed()).thenReturn(true);
        when(submitSelector.isDisplayed()).thenReturn(true);

        var reply1 = mock(WebElement.class);
        var reply2 = mock(WebElement.class);
        when(browser.findElements(By.cssSelector(config.outputSelector()))).thenReturn(List.of(reply1, reply2));

        String query = "Hello LLM, are you there?";
        when(reply2.getText()).thenReturn("Indeed I am");

        var result = chatbot.send(query);

        InOrder order = Mockito.inOrder(browser, inputSelector,submitSelector);
        order.verify(browser).executeScript(eq(JS_ADD_TEXT_TO_INPUT), eq(inputSelector), eq(query));
        order.verify(inputSelector).sendKeys(eq(" "));
        order.verify(submitSelector).click();

        assertEquals("Indeed I am", result);
    }


}