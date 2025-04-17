package com.marcha.Messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.marcha.Messaging.event.BentoEvent;

@ApplicationScoped
public class BentoEventProducer {

    Logger logger = Logger.getLogger(getClass());

    @Inject
    @Channel("bento-event")
    Emitter<BentoEvent> emitter;

    public void sendBentoEvent(BentoEvent bentoEvent) {
        logger.debug("BentoEventProducer sendBentoEvent: start! bentoEvent = " + bentoEvent);
        emitter.send(bentoEvent);
        logger.debug("BentoEventProducer sendBentoEvent: OK bentoEvent = " + bentoEvent);
    }
}