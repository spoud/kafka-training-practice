package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import io.smallrye.reactive.messaging.kafka.companion.ProducerTask;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.ConsumerGroupState;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTestResource(KafkaCompanionResource.class)
@QuarkusTest
public class ConsumerTest {

    @InjectKafkaCompanion
    KafkaCompanion companion;

    @InjectMock
    @RestClient
    SampleRestClient sampleRestClient;

    @Before
    public void setup() {
        when(sampleRestClient.getEchoPingMessage(any(PingMessage.class))).thenReturn(new PingMessage());
    }
    @Test
    public void testConsumerIsCallingRestEndpoint() {
        System.out.println("Waiting for consumer to be connected");
        await().timeout(Duration.ofSeconds(60)).until(() -> companion.consumerGroups().list().stream().anyMatch(g -> {
            int assignedTopicPartitions = companion.consumerGroups().describe("4.1-solution-quarkus-consume-call-rest").members().stream().map(m -> m.assignment().topicPartitions().size()).reduce(0, Integer::sum);
            System.out.println("Consumer group " + g.groupId() + " state " + g.state() + " assigned TopicPartitions:  " + assignedTopicPartitions);
            return g.groupId().equals("4.1-solution-quarkus-consume-call-rest") && assignedTopicPartitions > 0;
        }));
        System.out.println("Consumer connected " + companion.consumerGroups().list());
        ProducerTask producerTask = companion.produceWithSerializers(PingMessageSerializer.class).usingGenerator(i -> new ProducerRecord<>("kaf-demo-ping-json-3", new PingMessage(999)),1);
        producerTask.awaitCompletion();
        verify(sampleRestClient, timeout(5000).times(1)).getEchoPingMessage(any(PingMessage.class));
    }
}