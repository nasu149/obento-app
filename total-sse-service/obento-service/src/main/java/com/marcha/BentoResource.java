// src/main/java/com/example/BentoResource.java
package com.marcha;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import com.marcha.Messaging.BentoEventProducer;
import com.marcha.Messaging.event.BentoEvent;
import com.marcha.Messaging.utils.Record2EventPojo;
import com.marcha.entity.BentoRecord;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.transactions.KafkaTransactions;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/records")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BentoResource {

    @Inject
    BentoEventProducer bentoEventProducer;

    @Channel("bento-event")
    KafkaTransactions<BentoEvent> kafkaTx;

    Logger logger = Logger.getLogger(getClass());

    @GET
    public List<BentoRecord> getAll() {
        // 本来は userId を取得して filter する（後でKeycloak対応）
        return BentoRecord.listAll();
    }

    @POST
    @Transactional
    public BentoRecord create(BentoRecord record) {
        BentoRecord bentoRecord =
            kafkaTx.withTransaction(emitter -> {
                logger.debug("BentoRecord create start! record = " + record);
                record.persist();
                logger.debug("BentoRecord create persist end = " + record);

                BentoEvent bentoEvent = Record2EventPojo.record2EventPojo(record, BentoEvent.EventType.CREATED);
                logger.debug("BentoRecord create record2Event" + bentoEvent);

                emitter.send(bentoEvent);
                logger.debug("BentoRecord create sendBentoEvent OK " + bentoEvent);

                return Uni.createFrom().item(record);
            }).await().indefinitely();

            return bentoRecord;
    }

    @PUT
    @Path("/{date}")
    @Transactional
    public BentoRecord update(@PathParam("date") String date, BentoRecord update) {
        logger.debug("BentoRecord update: start! date = " + date);
        LocalDate targetDate = LocalDate.parse(date);
        BentoRecord record = BentoRecord.find("userId = ?1 and date = ?2", update.userId, targetDate).firstResult();
        logger.debug("BentoRecord update: find record = " + record);

        if (record == null) {
            throw new NotFoundException();
        }
        logger.debug("BentoRecord update: not 404 = " + record);

        record.who = update.who;

        // bentEvent を allwance にメッセージングを飛ばす
        BentoEvent bentoEvent = Record2EventPojo.record2EventPojo(record, BentoEvent.EventType.UPDATED);
        bentoEventProducer.sendBentoEvent(bentoEvent);

        return record;
    }

    @DELETE
    @Path("/{date}")
    @Transactional
    public void delete(@PathParam("date") String date, @QueryParam("userId") String userId) {
        LocalDate targetDate = LocalDate.parse(date);
        BentoRecord.delete("userId = ?1 and date = ?2", userId, targetDate);

        // bentEvent を allwance にメッセージングを飛ばす
        BentoEvent bentoEvent = Record2EventPojo.record2EventPojo(null, BentoEvent.EventType.DELETED);
        bentoEventProducer.sendBentoEvent(bentoEvent);
    }
}