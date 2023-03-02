package io.spoud.training;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Properties;
import java.util.Random;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    public static final String TOPIC = "employees-avro";

    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ACKS_CONFIG, "all");
        props.put(KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("schema.registry.url", "http://localhost:8081");

        try (KafkaProducer<EmployeeId, Employee> producer = new KafkaProducer<>(props)) {
            while (true) {
                ProducerRecord<EmployeeId, Employee> record = createRandomRecord();
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        LOGGER.error("failed to commit", exception);
                        System.exit(1);
                    } else {
                        LOGGER.info("Committed message offset={}, partition={}, value={}", metadata.offset(), metadata.partition(), record.value());
                    }
                });
                Thread.sleep(500);
            }
        }
    }

    private static ProducerRecord<EmployeeId, Employee> createRandomRecord() {
        EmployeeId key = EmployeeId.newBuilder().setShortCode(randomShortcode()).build();
        Employee value = Employee.newBuilder()
                .setShortCode(key.getShortCode())
                .setFirstName(randomName())
                .setStartDate(randomStartDate())
                .setRole(JobRole.values()[random.nextInt(JobRole.values().length)])
                .build();
        return new ProducerRecord<>(TOPIC, key, value);
    }

    private static LocalDate randomStartDate() {
        return LocalDate.now().minusMonths(random.nextInt(100));
    }

    private static String randomShortcode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder shortcode = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            shortcode.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return shortcode.toString();
    }

    private static String randomName() {
        String[] names = {"Marie", "Maria", "Sophie", "Paul", "Noah", "Ben", "Jonas", "Mila", "Marie", "Emilia", "Felix", "Emma", "Leon", "Mia", "Anna", "Alexander", "Maximilian", "Maximilian", "Lena", "Elias", "Anton", "Ella", "Emil", "Sophie", "Lea", "Jonathan"};
        return names[random.nextInt(names.length)];
    }
}
