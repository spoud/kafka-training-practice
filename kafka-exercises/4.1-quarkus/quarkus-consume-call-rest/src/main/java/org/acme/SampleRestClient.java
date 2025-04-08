package org.acme;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
@RegisterRestClient
public interface SampleRestClient {
    @POST
    @Path("/echo")
    @Produces("application/json")
    PingMessage getEchoPingMessage(PingMessage message);
}
