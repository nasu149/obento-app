package com.marcha.Messaging.event;

import java.time.LocalDate;

public class BentoEventPayload {
    public String userId;

    public LocalDate date;

    public Who who;

    @Override
    public String toString() {
        return "BentoEventPayload [userId=" + userId + ", date=" + date + ", who=" + who + "]";
    }

    public static enum Who {
        self, mom, buy
    }
}
