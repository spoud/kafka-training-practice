package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.smallrye.config.ConfigLogging.log;

@Path("/")
public class KafkaPingResource {


    // TODO emitter

    @GET
    @Path("/ping-kafka-json")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pingKafkaJson() {
        log.info("ping kafka json");

        // TODO: send a message to kafka
        // TODO: return a response 200 "ok pong-kafka json"
        return Response.ok().build();
    }
}
