package com.file.kafka.broker.worker;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class Listener {

    @Incoming("words-in")
    public void sink(String word) {
        System.out.println(">> " + word);
    }
}
