package me.andrz.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by anders on 11/21/15.
 */
public class JsonReferenceCli {

    public static void main(String[] args) throws ParseException, JsonReferenceException, IOException {

        Options options = new Options();
        Option helpOption = new Option("help", null);
        options.addOption(helpOption);

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        Boolean help = commandLine.hasOption("help");

        if (help) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("json-reference", options);
            return;
        }

        List<String> argsList = commandLine.getArgList();
        File file = new File(argsList.get(0));

        JsonReferenceProcessor processor = new JsonReferenceProcessor();
        JsonNode jsonNode = processor.process(file);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(System.out, jsonNode);
        System.out.println();
    }

}
