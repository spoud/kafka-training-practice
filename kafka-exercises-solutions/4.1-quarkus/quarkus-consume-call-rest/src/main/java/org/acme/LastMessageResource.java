package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class LastMessageResource {

    @Inject
    StateStore stateStore;

    @GET
    @Path("/last-messages")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLastState() {
        StringBuilder result = new StringBuilder();
        if(stateStore.get() == null) {
            return "No messages received";
        }
        stateStore.get().forEach((k,v) -> result.append("partition ").append(k).append(" ").append(v).append("\n"));
        return result.toString();

    }
}
