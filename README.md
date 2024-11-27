# Chatbot API Wrapper

Exposes a JSON API for a web chatbot for testing with the Mindgard CLI.

Accepts HTTP POST of type `application/json` to `/chatbot` in the form `{"system_prompt": "{system_prompt}", "prompt":"{prompt}"}` where `{system_prompt}` and `{prompt}` are placeholders for the system prompt and prompt to use for testing.

Responds with a JSON array with one element containing the chatbot's response. e.g. `["Hello I am an LLM, how can I help you?"]`

This can be used as-is with simple chatbots such as [HuggingChat](https://huggingface.co/chat/), or used as a base to change to suit your webapp, 
or simply an example of how to expose a suitable API from your existing browser automation to connect the Mindgard CLI.

## Building

Requires Java 22+, Maven, and [a chromedriver binary](https://developer.chrome.com/docs/chromedriver/downloads) in the current working directory.

Build with `mvn package`

## Running 

Usage example for [HuggingChat](https://huggingface.co/chat/)

```
java -jar target/chatbot-api-wrapper-jar-with-dependencies.jar \
 --url "https://example.com/your-chatbot-url" \
 --ready-selector "button.w-full" \
 --input-selector "textarea" \
 --submit-selector "button[type=submit][class~=btn]" \
 --output-selector "div.prose" 
 ```

Then interact with 
```
$ curl -vv -XPOST http://localhost:9001/chatbot -d '{"system_prompt":"Please help", "prompt": "Hello LLM, are you there?"}'
["Hello, how can I help?"]
```
 
Then point the mindgard CLI at `localhost:9001/chatbot` . An example compatible mindgard.toml is provided: `mindgard test --config mindgard.toml`

## Options

`--parallelism` The number of browser/chatbot instances to use at once. Default 5.

`--url` This is the URL to load in the browser to open the chatbot

`--ready-selector` A CSS selector of an element to wait for presence of before attempting to interact with the chatbot. 

`--input-selector` A CSS selector for the input to the chatbot, where you would type your messages.

`--submit-selector` A CSS selector for the submit button to send message to the chatbot.

`--output-selector` A CSS selector to match the responses from the LLM. The last matching element will be used. 

## Customisation

This example is flexible enough to support a number of example open source chatbot projects.

For testing custom and more complex applications it is likely you will need to implement your own page interactions in Chatbot.java and rebuild.