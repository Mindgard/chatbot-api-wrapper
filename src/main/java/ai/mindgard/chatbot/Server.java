package ai.mindgard.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import spark.Spark;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Server {
    private final ArrayBlockingQueue<Chatbot> botQueue;
    private BiFunction<ChatbotConfig, Integer, Chatbot> chatbotFactory;

    public Server(ChatbotConfig config, int parallelism) throws InterruptedException {
        this(config, parallelism, Chatbot::new);
    }
    public Server(ChatbotConfig config, int parallelism, BiFunction<ChatbotConfig,Integer,Chatbot> chatbotFactory) throws InterruptedException {
        this.botQueue = new ArrayBlockingQueue<Chatbot>(parallelism);
        this.chatbotFactory = chatbotFactory;

        for (int i = 0; i < parallelism; i++) {
            botQueue.put(chatbotFactory.apply(config,i));
        }
    }

    private void start() {
        Spark.port(9001);
        Spark.post("/chatbot",(req, res) -> queryChatbot(req.body()));
    }

    public String queryChatbot(String body) throws InterruptedException, JsonProcessingException {
        var om = new ObjectMapper();
        var chatbot = botQueue.take();
        try {
            var request = om.readValue(body, LLMRequest.class);

            return toJson(chatbot.send(request.prompt));
        } finally {
            botQueue.put(chatbot);
        }
    }


    public static void main(String... args) throws InterruptedException {
        new Server(ChatbotConfig.readFrom(args), 5).start();
    }


    private static String toJson(String response) {
        try {
            return new ObjectMapper().writeValueAsString(List.of(response));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    record LLMRequest(String system_prompt, String prompt) {}





}