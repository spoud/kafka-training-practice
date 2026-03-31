package io.spoud.kafka_standby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Service
public class KafkaHelloWorldClient {

    private static final Logger logger = LoggerFactory.getLogger(KafkaHelloWorldClient.class);
    private static final String TOPIC = "example-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final java.util.concurrent.atomic.AtomicBoolean messageReceived = new java.util.concurrent.atomic.AtomicBoolean(false);

    public KafkaHelloWorldClient(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 1000)
    public void produceHelloWorld() {
        String message = "Hello World";
        try {
            kafkaTemplate.send(TOPIC, message);
            logger.info("Produced message: {} to topic: {}", message, TOPIC);
        } catch (StandbyAwareProducer.ProducerOnStandbyException ex) {
            logger.warn(ex.getMessage());
        }
    }

    @KafkaListener(topics = TOPIC, groupId = "kafka-standby-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeHelloWorld(String message) {
        logger.info("Consumed message: {} from topic: {}", message, TOPIC);
        messageReceived.set(true);
    }
}
