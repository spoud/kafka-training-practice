package org.acme;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class PingMessageSerializer extends ObjectMapperSerializer<PingMessage> {
    public PingMessageSerializer() {
        super();
    }
}
