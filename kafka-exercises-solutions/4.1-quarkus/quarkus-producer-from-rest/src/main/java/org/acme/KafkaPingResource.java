package org.acme;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;


import java.time.Duration;


@Path("/")
public class KafkaPingResource {

    private static final Logger LOG = org.jboss.logging.Logger.getLogger(KafkaPingResource.class);
    @Inject
    @Channel("kaf-demo-ping-json")
    Emitter<Record<String, PingMessage>> pingMessageEmitter;

    @GET
    @Path("/ping-kafka-json")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pingKafkaJson(@QueryParam("number") int number) {

        LOG.info("ping kafka json number: " + number);

        PingMessage pingMessage = new PingMessage();
        pingMessage.setNumber(number);
        Record<String, PingMessage> msg = Record.of("ping-key-" + number, pingMessage);
        return Uni.createFrom()
                .completionStage(pingMessageEmitter.send(msg))
                .replaceWith(Response.status(Response.Status.OK).entity("ok pong-kafka json").build())
                .onFailure()
                .recoverWithItem(t -> Response.status(Response.Status.BAD_REQUEST).entity(t.getMessage()).build())
                .await().atMost(Duration.ofSeconds(20));
    }
}
