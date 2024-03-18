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
        // TODO: create a producer and send some messages then verify that they are received
    }

}
