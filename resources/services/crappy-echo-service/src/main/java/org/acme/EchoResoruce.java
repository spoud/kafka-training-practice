package org.acme;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Random;


@Path("/")
public class EchoResoruce {

    private static final Logger LOG = Logger.getLogger(EchoResoruce.class);

    Random random = new Random();

    @GET
    @Path("/hello")
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    @GET
    @Path("/echo")
    public Response echo(byte[] body) {
        LOG.info("echoPost" + Arrays.toString(body));
        Response response = getCrappyEchoResponse(body);
        LOG.info("echoPost" + Arrays.toString(body) + " returning" + response.getStatus());
        return response;
    }


    @POST
    @Path("/echo")
    public Response echoPost(byte[] body) {
        LOG.info("echoPost" + Arrays.toString(body));
        Response response = getCrappyEchoResponse(body);
        LOG.info("echoPost" + Arrays.toString(body) + " returning" + response.getStatus());
        return response;
    }

    private Response getCrappyEchoResponse(byte[] body) {
        try {
            return switch (random.nextInt(1, 50)) {
                case 1 -> {
                    Thread.sleep(10_000);
                    yield Response.status(500).build();
                }
                case 2 -> {
                    Thread.sleep(30_000);
                    yield Response.status(503).build();
                }
                case 3 -> Response.status(400).build();
                case 4 -> Response.status(401).build();
                case 5 -> Response.status(403).build();
                case 6 -> Response.status(404).build();
                case 9 -> Response.status(408).build();
                default -> {

                    Thread.sleep(2_000);
                    yield Response.ok().entity(body).build();
                }

            };
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}