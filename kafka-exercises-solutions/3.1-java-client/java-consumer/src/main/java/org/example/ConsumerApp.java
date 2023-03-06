package org.example;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;


public class ConsumerApp
{
    public static void main( String[] args )
    {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test1");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        try(Consumer<String, String> consumer = new KafkaConsumer<>(props)){
            consumer.subscribe(List.of("hello-from-java"));
            while(true){
                consumer.poll(Duration.ofSeconds(1)).forEach(record -> {
                    System.out.println(record.key() + " " + record.value());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println( "The End!" );
    }
}
