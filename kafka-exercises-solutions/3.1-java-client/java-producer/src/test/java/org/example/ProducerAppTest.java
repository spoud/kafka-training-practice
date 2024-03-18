package org.example;

import junit.framework.TestCase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ProducerAppTest
    extends TestCase
{
    KafkaContainer kafka;
    @Before
    public void setUp() throws Exception {
        kafka= new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));
        kafka.start();
    }
    @Test
    public void testApp()
    {
        Properties props = ProducerApp.getDefaultProps();
        props.put("bootstrap.servers", kafka.getBootstrapServers());
        ProducerApp.produce(props);

        // verify

        Map<String,String> events = new HashMap<>();

        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", kafka.getBootstrapServers());
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("group.id", "test1");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("auto.offset.reset", "earliest");


        try(Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps)){
            consumer.subscribe(List.of("hello-from-java"));
            int i = 5;
            while(i-- > 0){
                consumer.poll(Duration.ofSeconds(1)).forEach(record -> {
                    events.put(record.key(), record.value());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(100, events.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(Integer.toString(i), events.get(Integer.toString(i)));
        }

    }

}
