package org.acme;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
@RegisterRestClient
public interface SampleRestClient {
    @POST
    @Path("/echo")
    PingMessage getEchoPingMessage(PingMessage message);
}
