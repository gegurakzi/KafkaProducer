package org.malachai.kafka.twitterAPI;

import org.apache.http.HttpEntity;
import org.malachai.kafka.twitterAPI.entity.TwitterHttpResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class BaseStream {

    protected BufferedReader connectStream(String bearerToken) throws IOException, URISyntaxException {
        BufferedReader reader = null;
        HttpEntity entity = TwitterHttpResponseEntity.getEntity("https://api.twitter.com/2/tweets/sample/stream", bearerToken);
        if (null != entity) {
            reader = new BufferedReader(new InputStreamReader((entity.getContent())));
        }
        return reader;
    }

    protected void testStream(String bearerToken) throws IOException, URISyntaxException {
        BufferedReader reader = null;
        if (null != bearerToken) {
            reader = connectStream(bearerToken);
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        } else {
            System.out.println("There was a problem getting your bearer token. Please make sure you set the BEARER_TOKEN environment variable");
        }
    }
}
