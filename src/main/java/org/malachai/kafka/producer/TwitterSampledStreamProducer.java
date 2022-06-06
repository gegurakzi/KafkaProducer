package org.malachai.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.*;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.malachai.kafka.twitterAPI.SampledStream;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
public class TwitterSampledStreamProducer {

    private Producer<String, String> producer;
    private Properties props;

    public TwitterSampledStreamProducer(){
        props = new Properties();
        props.put("bootstrap.servers", "192.168.0.14:9092");
        props.put("transactional.id", "my-transactional-id");
        producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());
        log.info("Initializing transactions...");
        producer.initTransactions();
        log.info("Tansactions initialized");
    }

    public void test_hundred(String topic) {
        try {
            log.info("Beginning transaction...");
            producer.beginTransaction();
            log.info("Transaction began");
            for (int i = 0; i < 100; i++) {
                producer.send(new ProducerRecord<>(topic, Integer.toString(i), Integer.toString(i)));
                log.info("Sending message: " + Integer.toString(i));
            }
            log.info("Commiting transaction...");
            producer.commitTransaction();
            log.info("Transaction commited");
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            log.error("closing the producer and exit: "+e);
            producer.close();
        } catch (KafkaException e) {
            // For all other exceptions, just abort the transaction and try again.
            log.warn("aborting the transaction and try again: "+e);
            producer.abortTransaction();
        }
    }

    public void launch(String topic, String bearerToken){
        try {
            producer.beginTransaction();

            SampledStream sampledStream = new SampledStream();
            BufferedReader reader = sampledStream.getStreamReader(bearerToken);

            int i = 0;
            String line = reader.readLine();
            while (line != null) {
                producer.send(new ProducerRecord<>(topic, Integer.toString(i), line));
                i++;
                line = reader.readLine();
            }
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException | IOException | URISyntaxException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            log.error("closing the producer and exit: "+e);
            producer.close();
        } catch (KafkaException e) {
            // For all other exceptions, just abort the transaction and try again.
            log.warn("aborting the transaction and try again: "+e);
            producer.abortTransaction();
        }
    }

    public void close(){
        producer.close();
    }

}
