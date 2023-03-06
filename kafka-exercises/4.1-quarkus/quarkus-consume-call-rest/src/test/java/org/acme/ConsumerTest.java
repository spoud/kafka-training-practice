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


    @Before
    public void setup() {
        // TODO: setup if needed
    }
    @Test
    public void testConsumerIsCallingRestEndpoint() {
        // TODO: verify that the consumer is calling the rest endpoint
    }
}