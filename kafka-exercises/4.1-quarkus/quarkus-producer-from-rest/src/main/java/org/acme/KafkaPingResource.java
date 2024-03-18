package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/")
public class KafkaPingResource {
    private static final Logger LOG = Logger.getLogger(KafkaPingResource.class);


    // TODO emitter

    @GET
    @Path("/ping-kafka-json")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pingKafkaJson() {
        LOG.info("ping kafka json");

        // TODO: send a message to kafka
        // TODO: return a response 200 "ok pong-kafka json"
        return Response.ok().build();
    }
}
