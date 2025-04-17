package com.marcha;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/total")
public class TotalResource {

    Logger logger = Logger.getLogger(getClass());

    @Channel("calc-total")
    Multi<Integer> total;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS) // denotes that server side events (SSE) will be produced
    public Multi<Integer> stream() {
        logger.debug("TotalResouce stream: start total = " + total);
        return total.log();
    }
}
