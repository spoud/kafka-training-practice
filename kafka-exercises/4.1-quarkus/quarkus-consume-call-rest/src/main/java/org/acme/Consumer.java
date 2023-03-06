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


    // TODO: implement consumer
    public void consume(/* TODO ...*/) {
    }
}