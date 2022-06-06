package org.malachai.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.malachai.kafka.producer.TwitterSampledStreamProducer;
import org.malachai.kafka.twitterAPI.FilteredStream;
import org.malachai.kafka.twitterAPI.SampledStream;


import java.io.*;
import java.util.Properties;

@Slf4j
public class KafkaApplication {

    private static String consumer_key;
    private static String consumer_secret;
    private static String access_token;
    private static String access_token_secret;
    private static String bearer_token;

    private static void loadConfig(String path) throws FileNotFoundException {
        Properties prop = new Properties();
        FileInputStream s = new FileInputStream(path);
        try{
            prop.load(s);
        } catch (IOException e){
            e.printStackTrace();
        }
        consumer_key = prop.getProperty("consumer_key");
        consumer_secret = prop.getProperty("consumer_secret");
        access_token = prop.getProperty("access_token");
        access_token_secret = prop.getProperty("access_token_secret");
        bearer_token = prop.getProperty("bearer_token");
    }

    private static CommandLine argParser(String[] args){
        Options options = new Options();     // Options Arguments which are Acceptable By Program.
        Option key = new Option("c", "key", true, "keys and secrets path");
        options.addOption(key);
        CommandLineParser parser = new BasicParser();
        // use to read Command Line Arguments
        HelpFormatter formatter = new HelpFormatter();  // // Use to Format
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);  //it will parse according to the options and parse option value
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("\n-c  (path of .properties file with tokens and secret keys)", options);
            System.exit(1);
        }
        return cmd;
    }

    public static void main(String[] args) throws Exception {
        CommandLine cmd = argParser(args);
        String key_path = cmd.getOptionValue("key");
        if(key_path==null){
            key_path = System.getProperty("user.dir")+File.separator+"config.properties";
        }
        loadConfig(key_path);

        TwitterSampledStreamProducer twitterProducer = new TwitterSampledStreamProducer();
        twitterProducer.launch("Twitter-test", bearer_token);

        //SampledStream sampledStream = new SampledStream();
        //sampledStream.testStream(bearer_token);

        //FilteredStream filteredStream = new FilteredStream();
        //filteredStream.launch(bearer_token);

    }
}
