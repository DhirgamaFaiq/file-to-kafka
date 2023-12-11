package com.file.kafka.broker.worker;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class Producer {

    @Inject
    @Channel("words-out")
    Emitter<String> emitterProducXslx;
    public void send(String data) {
        emitterProducXslx.send(data);
    }
}
