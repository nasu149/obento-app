package com.marcha.Messaging;


import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.hibernate.TransactionException;
import org.jboss.logging.Logger;

import com.marcha.Messaging.event.BentoEvent;
import com.marcha.Messaging.utils.BentoEvent2AllowancePojo;
import com.marcha.entity.AllowanceEntity;
import com.marcha.repository.AllowanceRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BentoEventConsumer {
  
    Logger logger = Logger.getLogger(getClass());

    @Inject
    AllowanceRepository allowanceRepository;

    @Incoming("bento-event")
    @Outgoing("calc-total")
    @Transactional
    public Integer consumeBentoEvent(BentoEvent bentoEvent){
        
        logger.debug("BentoEventConsumer consumeBentoEvent: start bentEvent = " + bentoEvent);

        // 自分の型 AllowanceEntiry に変換
        AllowanceEntity allowanceEntity = BentoEvent2AllowancePojo.bentoEvent2AllowanceEntity(bentoEvent);
        logger.debug("BentoEventConsumer consumeBentoEvent: bentoEvent2AllowanceEntity! allowanceEntity = " + allowanceEntity);

        // DB に登録・更新・削除
        switch (bentoEvent.eventType) {
            case CREATED:
                persistEntity(allowanceEntity);
                break;
            case UPDATED:
                updateEntity(allowanceEntity);
                break;
            case DELETED:
                deleteEntity(allowanceEntity);
                break;
            default:
                throw new TransactionException("bentoEventType is null!");
        }

        // トータルを計算
        logger.debug("BentoEventConsumer debug list = " + allowanceRepository.findByUser(allowanceEntity.userId));
        Integer total = allowanceRepository.calculatePendingTotal(allowanceEntity.userId);
        logger.debug("BentoEventConsumer consumeBentoEvent:calculatePendingTotal OK  total = " + total);

        return total;
    }

    private void persistEntity(AllowanceEntity allowanceEntity){
        // DB に登録
        logger.debug("BentoEventConsumer persistEntity start. allowanceEntity = " + allowanceEntity);
        allowanceEntity.persist();
        logger.debug("BentoEventConsumer persistEntity: persist OK  allowanceEntity = " + allowanceEntity);
    }

    private void updateEntity(AllowanceEntity allowanceEntity) {
        logger.debug("BentoEventConsumer updateEntity start. allowanceEntity = " + allowanceEntity);
        AllowanceEntity record = AllowanceEntity.find("userId = ?1 and date = ?2", allowanceEntity.userId, allowanceEntity.date).firstResult();
        logger.debug("BentoEventConsumer updateEntity recort = " + record);

        record.who = allowanceEntity.who;
        record.earned = allowanceEntity.who.equals(AllowanceEntity.Who.self) ? 400 : 0;
    }

    private void deleteEntity(AllowanceEntity allowanceEntity) {
        logger.debug("BentoEventConsumer deleteEntity start. allowanceEntity = " + allowanceEntity);
        AllowanceEntity.delete("userId = ?1 and date = ?2", allowanceEntity.userId, allowanceEntity.date);
        logger.debug("BentoEventConsumer delete OK. allowanceEntity = " + allowanceEntity);
    }

}
