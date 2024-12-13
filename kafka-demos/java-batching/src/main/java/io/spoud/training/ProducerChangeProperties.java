package io.spoud.training;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class ProducerChangeProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerChangeProperties.class);

    public static void main(String[] args) {
        //java-batching-1 Create the producer
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("compression.type", "gzip");
        props.put("batch.size", 16384);
        props.put("linger.ms", 50);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // Send messages
        for (int i = 0; i < 10_000_000; i++) {
            String key = "stream-" + (i % 13);
            String value = "message-" + i;
            producer.send(new ProducerRecord<>("java-batching-5", key, value));
            if (/*some condition to react to high load*/ i == 10_0000) {
                LOGGER.info("Flushing and restarting the producer with new settings");
                producer.flush();
                producer.close();
                props.put("batch.size", 1638400);
                props.put("linger.ms", 5000);
                producer = new KafkaProducer<>(props);
            }
        }
    }
}
