package org.acme;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Emitter;


import java.time.Duration;

import static io.smallrye.config.ConfigLogging.log;

@Path("/")
public class KafkaPingResource {


    @Inject
    @Channel("kaf-demo-ping-json")
    Emitter<Record<String, PingMessage>> pingMessageEmitter;

    @GET
    @Path("/ping-kafka-json")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pingKafkaJson(@QueryParam("number") int number) {

        log.info("ping kafka json number: " + number);

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
