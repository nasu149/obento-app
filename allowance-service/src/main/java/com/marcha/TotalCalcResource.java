package com.marcha;

import java.util.List;

import org.jboss.logging.Logger;

import com.marcha.Messaging.utils.AllowanceList2TotalCalcPojo;
import com.marcha.entity.AllowanceEntity;
import com.marcha.repository.AllowanceRepository;
import com.marcha.resourcepojo.TotalCalcPojo;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/total")
public class TotalCalcResource {

    @Inject
    AllowanceRepository allowanceRepository;

    Logger logger = Logger.getLogger(getClass());

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public TotalCalcPojo calcTotal(@PathParam("userId") String userId) {
        logger.debug("calcTotal start userId = " + userId);
        List<AllowanceEntity> allowanceList = allowanceRepository.findByUser(userId);

        logger.debug("calcTotal allowanceRepository.findByUser(userId) = " + allowanceList);

        if (allowanceList.size() == 0) {
            logger.debug("calcTotal allowanceList.size() == 0 ");
            throw new NotFoundException("userId is invalid.");
        }

        // resource を作成
        TotalCalcPojo totalCalcPojo = AllowanceList2TotalCalcPojo.allowanceList2TotalCalcPojo(allowanceList);
        logger.debug("calcTotal totalCalcPojo = " + totalCalcPojo);

        return totalCalcPojo;
    }
}
