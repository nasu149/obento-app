package com.marcha;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import com.marcha.resourcepojo.TotalCalcPojo;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/total")
public class TotalResource {

    Logger logger = Logger.getLogger(getClass());

    @Channel("calc-total")
    Multi<TotalCalcPojo> totalCalcPojoMulti;

    @GET
    @Path("{userId}")
    @Produces(MediaType.SERVER_SENT_EVENTS) // denotes that server side events (SSE) will be produced
    public Multi<TotalCalcPojo> stream(@PathParam("userId") String userId) {
        logger.debug("TotalResouce stream: start totalCalcPojo = " + totalCalcPojoMulti);
        return totalCalcPojoMulti.filter(totalCalcPojo -> {
            boolean isMyUser = totalCalcPojo.userId.equals(userId);
            logger.debug("TotalResource stream totalCalcPojo = " + totalCalcPojo);
            logger.debug("TotalResource stream isMyUser = " + isMyUser);
            return isMyUser;
        });
    }
}
