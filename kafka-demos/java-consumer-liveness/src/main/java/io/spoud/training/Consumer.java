package io.spoud.training;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class Consumer {

    private static final Random random = new Random();
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        String groupId = "test" + random.nextLong();
        props.put(GROUP_ID_CONFIG, groupId);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");

        // the following settings are important for consumer liveness
        props.put(HEARTBEAT_INTERVAL_MS_CONFIG, 1000); // heartbeat every second
        props.put(SESSION_TIMEOUT_MS_CONFIG, 20000); // timeout when no heartbeat for 20s
        props.put(MAX_POLL_INTERVAL_MS_CONFIG, 20000); // poll should happen every 20s
        props.put(MAX_POLL_RECORDS_CONFIG, 5); // max records at a time

        // start 3 instances of this consumer group
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            executorService.submit(() -> startConsumer(finalI, props));
        }
        LOGGER.info("You can inspect the consumer group '{}' by running this command: 'watch kafka-consumer-groups --bootstrap-server localhost:9092  --describe --group {}'", groupId, groupId);
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    private static void startConsumer(int i, Properties consumerProperties) {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties)) {
            ConsumerRebalanceListener consumerRebalanceListener = new MyConsumerRebalanceListener(consumer);
            consumer.subscribe(List.of("consumer-liveness"), consumerRebalanceListener);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        LOGGER.debug("Calling external system for offset = {}, value = {}", record.offset(), record.value());
                        callExternalSystem(record.value());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * Simulate a blocking call to an external system
     * @param msg
     * @throws InterruptedException
     */
    private static void callExternalSystem(String msg) throws InterruptedException {
        int millis = random.nextInt(7000);
        LOGGER.debug("Blocking thread for {} ms", millis);
        Thread.sleep(millis);
    }

    private record MyConsumerRebalanceListener(KafkaConsumer<?, ?> consumer) implements ConsumerRebalanceListener {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            LOGGER.warn("Partitions revoked! " + partitions);
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            LOGGER.warn("Partitions assigned! " + partitions);
        }
    }
}
