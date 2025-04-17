// src/main/java/com/example/BentoResource.java
package com.marcha;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.hibernate.TransactionException;
import org.jboss.logging.Logger;

import com.marcha.Messaging.event.BentoEvent;
import com.marcha.Messaging.utils.Record2EventPojo;
import com.marcha.entity.BentoRecord;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.transactions.KafkaTransactions;
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
                logger.debug("BentoRecord create record2Event = " + bentoEvent);

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
        BentoRecord bentoRecord =
            kafkaTx.withTransaction(emitter -> {
                logger.debug("BentoRecord update: start! date = " + date);
                LocalDate targetDate = LocalDate.parse(date);
                BentoRecord record = BentoRecord.find("userId = ?1 and date = ?2", update.userId, targetDate).firstResult();
                logger.debug("BentoRecord update: find record = " + record);

                if (record == null) {
                    logger.debug("BentoRecord update: 404 = " + record);
                    throw new NotFoundException();
                }
                logger.debug("BentoRecord update: not 404 = " + record);
                
                record.who = update.who;
                logger.debug("BentoRecord update: update OK new record = " + record);
                
                // bentEvent を allwance にメッセージングを飛ばす
                BentoEvent bentoEvent = Record2EventPojo.record2EventPojo(record, BentoEvent.EventType.UPDATED);
                emitter.send(bentoEvent);
                logger.debug("BentoRecord update: emitter send OK. bentoEvent = " + bentoEvent);
                
                return Uni.createFrom().item(record);
            }).await().indefinitely();

            return bentoRecord;
    }

    @DELETE
    @Path("/{date}")
    @Transactional
    public void delete(@PathParam("date") String date, @QueryParam("userId") String userId) {
            kafkaTx.withTransaction(emitter -> {
                logger.debug("BentoRecord delete: start! date = " + date);
                LocalDate targetDate = LocalDate.parse(date);
                long deleteCount = BentoRecord.delete("userId = ?1 and date = ?2", userId, targetDate);
        
                if (deleteCount == 0) {
                    throw new TransactionException("not deleted");
                }
                
                logger.debug("BentoRecord delete: OK = " + targetDate);

                // bentEvent を allwance にメッセージングを飛ばす
                BentoEvent bentoEvent = Record2EventPojo.userDate2EventPojo(userId, targetDate, BentoEvent.EventType.DELETED);
                emitter.send(bentoEvent);
                logger.debug("BentoRecord delete: emitter send OK. bentoEvent = " + bentoEvent);

                return Uni.createFrom().voidItem();
  
            }).await().indefinitely();
    }
}