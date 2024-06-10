package org.acme;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.Map;

public class KafkaPingResourceFailureTestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Collections.singletonMap("mp.messaging.outgoing.kaf-demo-ping-json.bootstrap.servers", "test-does-not-exist:9092");
    }
}
