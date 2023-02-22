package io.spoud.training;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.*;

public class Producer
{
    public static void main( String[] args )
    {

        //java-batching-1 Create the producer
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("compression.type", "gzip");
        props.put("batch.size", 1638400);
        props.put("linger.ms", 5000);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // Send messages
        for (int i = 0; i < 10_000_000; i++) {
            String key = "stream-" + (i % 13);
            String value = "message-" + i;
            producer.send(new ProducerRecord<>("java-batching-4", key, value));
        }
    }
}
