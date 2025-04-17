package com.marcha.Messaging.utils;

import com.marcha.Messaging.event.BentoEvent;
import com.marcha.entity.AllowanceEntity;


public class BentoEvent2AllowancePojo {
    public static AllowanceEntity bentoEvent2AllowanceEntity(BentoEvent bentoEvent) {

        AllowanceEntity allowanceEntity = new AllowanceEntity();
        allowanceEntity.userId = bentoEvent.bentoEventPayload.userId;
        allowanceEntity.date = bentoEvent.bentoEventPayload.date;
        allowanceEntity.earned = bentoEvent.eventType.equals(BentoEvent.EventType.CREATED) ? 400 : 0;
        allowanceEntity.status = AllowanceEntity.Status.PENDING;
        allowanceEntity.who = AllowanceEntity.Who.valueOf(bentoEvent.bentoEventPayload.who.name());

        return allowanceEntity;
    }
}
