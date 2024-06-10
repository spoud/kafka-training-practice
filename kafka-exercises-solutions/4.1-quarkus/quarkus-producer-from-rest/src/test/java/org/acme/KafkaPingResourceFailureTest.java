package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
@TestProfile(KafkaPingResourceFailureTestProfile.class)
public class KafkaPingResourceFailureTest {

    @InjectKafkaCompanion
    KafkaCompanion companion;

    @Test
    public void testEndpoint() {
        given()
                .when().get("/ping-kafka-json")
                .then()
                .statusCode(500)
                .body(containsString("io.smallrye.mutiny.TimeoutException"));
    }

    @Test
    public void testEndpointFireAndForget() {

        given()
                .when().get("/ping-kafka-json-fire-and-forget")
                .then()
                .statusCode(500)
                .body(containsString("io.smallrye.mutiny.TimeoutException"));
    }
}