package org.malachai.kafka.twitterAPI;

import org.apache.http.HttpEntity;
import org.malachai.kafka.twitterAPI.entity.TwitterHttpResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;


public class SampledStream extends BaseStream{

    public BufferedReader getStreamReader(String bearerToken) throws IOException, URISyntaxException {
        BufferedReader reader = null;
        if (null != bearerToken) {
            reader = connectStream(bearerToken);
        } else {
            System.out.println("There was a problem getting your bearer token. Please make sure you set the BEARER_TOKEN environment variable");
        }
        return reader;
    }

}
