package ai.mindgard.chatbot;

import org.apache.commons.cli.*;

public record ChatbotConfig(
        String url,
        String readySelector,
        String inputSelector,
        String submitSelector,
        String outputSelector) {


    public static ChatbotConfig readFrom(String[] args) {
        Option urlOption = Option.builder("u")
                .required(true)
                .hasArg()
                .longOpt("url")
                .desc("URL to Chatbot to test")
                .build();
        Option readinessOption = Option.builder("r")
                .required(true)
                .hasArg()
                .longOpt("ready-selector")
                .desc("CSS selector to wait for readiness" )
                .build();
        Option inputOption = Option.builder("i")
                .required(true)
                .hasArg()
                .longOpt("input-selector")
                .desc("CSS selector for input field")
                .build();
        Option outputOption = Option.builder("o")
                .required(true)
                .hasArg()
                .longOpt("output-selector")
                .desc("CSS selector for the output field")
                .build();
        Option submitOption = Option.builder("s")
                .required(true)
                .hasArg()
                .longOpt("submit-selector")
                .desc("CSS selector for the submit button")
                .build();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption(urlOption);
        options.addOption(readinessOption);
        options.addOption(inputOption);
        options.addOption(outputOption);
        options.addOption(submitOption);

        try {
            CommandLine commandLine = parser.parse(options, args);

            return new ChatbotConfig(
                    commandLine.getOptionValue(urlOption),
                    commandLine.getOptionValue(readinessOption),
                    commandLine.getOptionValue(inputOption),
                    commandLine.getOptionValue(submitOption),
                    commandLine.getOptionValue(outputOption)
            );

        } catch (ParseException e) {
            new HelpFormatter().printHelp("chatbot", options);
            throw new RuntimeException(e);
        }
    }
}