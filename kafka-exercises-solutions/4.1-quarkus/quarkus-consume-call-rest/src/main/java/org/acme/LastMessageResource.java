package org.acme;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
