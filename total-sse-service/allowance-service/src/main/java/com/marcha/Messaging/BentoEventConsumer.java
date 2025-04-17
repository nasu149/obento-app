package com.marcha.Messaging;


import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
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
        
        // DB に登録
        allowanceEntity.persist();
        logger.debug("BentoEventConsumer consumeBentoEvent: persist OK  allowanceEntity = " + allowanceEntity);
        
        // トータルを計算
        logger.debug("BentoEventConsumer debug list = " + allowanceRepository.findByUser(allowanceEntity.userId));
        Integer total = allowanceRepository.calculatePendingTotal(allowanceEntity.userId);
        logger.debug("BentoEventConsumer consumeBentoEvent:calculatePendingTotal OK  total = " + total);

        return total;


    }

}
