package org.acme;

import io.smallrye.common.annotation.Blocking;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Consumer {
    private static final Logger LOG = Logger.getLogger(Consumer.class);
    @Inject
    @RestClient
    SampleRestClient sampleRestClient;

    @Inject
    StateStore stateStore;

    @Incoming("kaf-demo-ping-json")
    @Blocking
    @Retry(maxRetries = 10, delay = 10)
    public void consume(ConsumerRecord<String,PingMessage> record) {
        PingMessage pingMessage = record.value();
        LOG.info("Received ping message: " + pingMessage.getNumber());
        PingMessage echoPingMessage = sampleRestClient.getEchoPingMessage(pingMessage);
        stateStore.add(record.partition(), echoPingMessage);
    }
}