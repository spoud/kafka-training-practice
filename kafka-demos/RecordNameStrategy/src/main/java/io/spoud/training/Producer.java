package io.spoud.training;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.spoud.training.recordnamestrategy.CreateEvent;
import io.spoud.training.recordnamestrategy.UpdateEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;
import java.util.*;

import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class Producer
{
    public static void main( String[] args )
    {

        //java-batching-1 Create the producer
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("value.subject.name.strategy", "io.confluent.kafka.serializers.subject.TopicRecordNameStrategy");
        props.put("schema.registry.url", "http://localhost:8081");

        KafkaProducer<String, CreateEvent> createEventProducer = new KafkaProducer<>(props);
        KafkaProducer<String, UpdateEvent> updateEventProducer = new KafkaProducer<>(props);
        String key = "aboutSomething";
        String topic = "record-name-strategy";

        // Send 1 create message and 5 update messages
        CreateEvent value = CreateEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setName("Create Event ")
                .setCreated(Instant.now())
                .build();
        createEventProducer.send(new ProducerRecord<>(topic, key, value));
        createEventProducer.flush();
        createEventProducer.close();
        for (int i = 0; i < 5; i++) {
            UpdateEvent updateValue = UpdateEvent.newBuilder()
                    .setEventId(UUID.randomUUID().toString())
                    .setUpdateCounter(i)
                    .setUpdated(Instant.now())
                    .build();
            updateEventProducer.send(new ProducerRecord<>(topic, key, updateValue));
        }
        updateEventProducer.flush();
        updateEventProducer.close();
    }
}
