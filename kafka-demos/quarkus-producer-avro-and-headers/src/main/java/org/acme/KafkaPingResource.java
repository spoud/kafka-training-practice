package org.acme;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import io.spoud.training.PingMessage;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.time.Duration;


@Path("/")
public class KafkaPingResource {

    private static final Logger LOG = org.jboss.logging.Logger.getLogger(KafkaPingResource.class);
    @Inject
    @Channel("kaf-demo-ping-avro")
    Emitter<Record<String, PingMessage>> pingMessageEmitter;
    @Channel("kaf-demo-ping-avro-header")
    Emitter<PingMessage> pingMessageEmitterHeader;

    @GET
    @Path("/ping-kafka-avro")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pingKafkaAvro(@QueryParam("number") int number) {

        LOG.info("ping kafka avro number: " + number);

        PingMessage pingMessage = new PingMessage();
        pingMessage.setNumber(number);
        Record<String, PingMessage> msg = Record.of("ping-key-" + number, pingMessage);

        return Uni.createFrom()
                .completionStage(pingMessageEmitter.send(msg))
                .replaceWith(Response.status(Response.Status.OK).entity("ok pong-kafka avro").build())
                .onFailure()
                .recoverWithItem(t -> Response.status(Response.Status.BAD_REQUEST).entity(t.getMessage()).build())
                .await().atMost(Duration.ofSeconds(20));
    }


    @GET
    @Path("/ping-kafka-avro-header")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pingKafkaJson(@QueryParam("number") int number) {

        LOG.info("ping kafka avro number: " + number);

        PingMessage pingMessage = new PingMessage();
        pingMessage.setNumber(number);
        OutgoingKafkaRecordMetadata<Object> metadata = OutgoingKafkaRecordMetadata.builder()
                .withHeaders(new RecordHeaders().add("my-header", "my-header-value".getBytes(StandardCharsets.UTF_8)))
                .withKey("ping-key-" + number)
                .build();

        Message<PingMessage> msg = Message.of(pingMessage).addMetadata(metadata);
        pingMessageEmitterHeader.send(msg);

        return Uni.createFrom()
                .completionStage(msg.ack())
                .replaceWith(Response.status(Response.Status.OK).entity("ok pong-kafka avro header").build())
                .onFailure()
                .recoverWithItem(() -> Response.status(Response.Status.BAD_REQUEST).entity(msg).build())
                .await().atMost(Duration.ofSeconds(20));
    }

}
