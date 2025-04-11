package com.prashanth.url_shortener.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClickLoggerProducer {

    private static final String TOPIC = "url-clicks";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void logClick(String payload) {
        kafkaTemplate.send(TOPIC, payload);
        System.out.println("📤 Kafka Event Sent: " + payload);
    }
}
