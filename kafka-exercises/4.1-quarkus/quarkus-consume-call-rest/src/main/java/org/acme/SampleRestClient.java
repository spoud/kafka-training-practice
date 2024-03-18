package org.acme;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
@RegisterRestClient
public interface SampleRestClient {
    @POST
    @Path("/echo")
    PingMessage getEchoPingMessage(PingMessage message);
}
