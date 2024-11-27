package ai.mindgard.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerTest {
    @Test
    public void should_round_robin_requests_to_chatbot_instances() throws InterruptedException, JsonProcessingException {
        var config = new ChatbotConfig(
            "https://example.com/bot",
            ".ready-selector",
            ".input-selector",
            ".submit-selector",
            ".output-selector"
        );
        var chatbots = List.of(mock(Chatbot.class), mock(Chatbot.class));
        int parallelism = chatbots.size();
        var botsIterator = chatbots.iterator();

        var server = new Server(config, parallelism, (c, id) -> botsIterator.next());
        String query = "Hello LLM, are you there?";
        when(chatbots.get(0).send(query)).thenReturn("Yes");
        when(chatbots.get(1).send(query)).thenReturn("Indeed");

        String message = "{\"system_prompt\":\"sprompt\", \"prompt\":\""+query+"\"}";

        assertEquals(json("Yes"), server.queryChatbot(message));
        assertEquals(json("Indeed"), server.queryChatbot(message));
        assertEquals(json("Yes"), server.queryChatbot(message));
        assertEquals(json("Indeed"), server.queryChatbot(message));
    }

    private String json(String m) {
        return "[\"" + m + "\"]";
    }
}