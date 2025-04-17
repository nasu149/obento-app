package com.marcha.Messaging.utils;

import java.time.LocalDate;

import com.marcha.Messaging.event.BentoEvent;
import com.marcha.Messaging.event.BentoEventPayload;
import com.marcha.entity.BentoRecord;

public class Record2EventPojo {
    public static BentoEventPayload record2EventPayloadPojo(BentoRecord record) {
        BentoEventPayload bentoEventPayload = new BentoEventPayload();
        bentoEventPayload.userId = record.userId;
        bentoEventPayload.date = record.date;
        bentoEventPayload.who = BentoEventPayload.Who.valueOf(record.who.name());

        return bentoEventPayload;
    }

    public static BentoEvent record2EventPojo(BentoRecord record, BentoEvent.EventType eventType) {
        BentoEventPayload bentoEventPayload = record2EventPayloadPojo(record);

        BentoEvent bentoEvent = new BentoEvent();
        bentoEvent.bentoEventPayload = bentoEventPayload;
        bentoEvent.eventType = eventType;

        return bentoEvent;
    }

    public static BentoEvent userDate2EventPojo(String userId, LocalDate date, BentoEvent.EventType eventType) {
        
        BentoEventPayload bentoEventPayload = new BentoEventPayload();
        bentoEventPayload.userId = userId;
        bentoEventPayload.date = date;
        bentoEventPayload.who = null;

        BentoEvent bentoEvent = new BentoEvent();
        bentoEvent.bentoEventPayload = bentoEventPayload;
        bentoEvent.eventType = eventType;

        return bentoEvent;
    }
}
