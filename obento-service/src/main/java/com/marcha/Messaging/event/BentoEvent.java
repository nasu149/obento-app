package com.marcha.Messaging.event;

public class BentoEvent {

    public EventType eventType;

    public BentoEventPayload bentoEventPayload;    

    @Override
    public String toString() {
        return "BentoEvent [eventType=" + eventType + ", bentoEventPayload=" + bentoEventPayload + "]";
    }

    public static enum EventType {
        CREATED, UPDATED, DELETED
    }
}
