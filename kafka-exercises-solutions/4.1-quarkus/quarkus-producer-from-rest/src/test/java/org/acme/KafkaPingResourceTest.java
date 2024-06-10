package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class KafkaPingResourceTest {

    @InjectKafkaCompanion
    KafkaCompanion companion;

    @Test
    public void testEndpoint() {
        given()
          .when().get("/ping-kafka-json")
          .then()
             .statusCode(200)
             .body(is("ok pong-kafka json"));

        // verify the message was sent to kafka
        ConsumerTask<String, String> pingMessages = companion.consumeStrings().fromTopics("kaf-demo-ping-json-3", 1, Duration.ofSeconds(10));
        pingMessages.awaitCompletion();
        assertEquals(1, pingMessages.count());

        // with pingMessages.stream().iterator().next() you can get the message
    }



    @Test
    public void testEndpointFireAndForget() {

        given()
                .when().get("/ping-kafka-json-fire-and-forget")
                .then()
                .statusCode(200);

        // verify the message was sent to kafka
        ConsumerTask<String, String> pingMessages = companion.consumeStrings().fromTopics("kaf-demo-ping-json-3", 1, Duration.ofSeconds(10));
        pingMessages.awaitCompletion();
        assertEquals(1, pingMessages.count());

        // with pingMessages.stream().iterator().next() you can get the message
    }
}