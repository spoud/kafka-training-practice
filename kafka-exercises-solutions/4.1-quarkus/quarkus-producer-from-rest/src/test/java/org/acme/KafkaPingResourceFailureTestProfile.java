package org.acme;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.Map;

public class KafkaPingResourceFailureTestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Collections.singletonMap("mp.messaging.outgoing.kaf-demo-ping-json.bootstrap.servers", "127.0.0.1:80"); // intentionally wrong port
    }
}
