package com.prashanth.url_shortener.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prashanth.url_shortener.entity.ClickEventEntity;
import com.prashanth.url_shortener.repository.ClickEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClickEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ClickEventRepository repository;

    @KafkaListener(topics = "url-clicks", groupId = "url-tracker")
    public void consume(String message) {
        try {
            ClickEvent event = objectMapper.readValue(message, ClickEvent.class);

            // Save to DB
            ClickEventEntity entity = new ClickEventEntity();
            entity.setShortCode(event.getShortCode());
            entity.setIp(event.getIp());
            entity.setUserAgent(event.getUserAgent());
            entity.setTimestamp(LocalDateTime.parse(event.getTimestamp()));

            repository.save(entity);

            System.out.println("✅ Saved ClickEvent to DB for: " + event.getShortCode());


        } catch (Exception e) {
            System.err.println("❌ Failed to process message: " + message);
            e.printStackTrace();
        }
    }
}
