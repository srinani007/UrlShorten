package com.prashanth.url_shortener.service;

import com.prashanth.url_shortener.model.ShortLink;
import com.prashanth.url_shortener.repository.ShortLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlShortenerService {

    @Autowired
    private ShortLinkRepository repository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "short-url:";

    public String shortenUrl(String longUrl) {
        String shortCode = generateShortCode();

        ShortLink link = new ShortLink();
        link.setShortCode(shortCode);
        link.setLongUrl(longUrl);
        repository.save(link);

        redisTemplate.opsForValue().set(PREFIX + shortCode, longUrl);
        System.out.println("🧠 CACHED IN REDIS: short-url:" + shortCode + " → " + longUrl);
        redisTemplate.opsForValue().set("short-url:" + shortCode, longUrl, Duration.ofHours(1));



        return shortCode;

    }

    public Optional<ShortLink> getOriginalUrl(String shortCode) {
        String cachedUrl = redisTemplate.opsForValue().get(PREFIX + shortCode);
        if (cachedUrl != null) {
            ShortLink link = new ShortLink();
            link.setShortCode(shortCode);
            link.setLongUrl(cachedUrl);
            return Optional.of(link);
        }

        Optional<ShortLink> dbResult = repository.findByShortCode(shortCode);
        dbResult.ifPresent(link -> redisTemplate.opsForValue().set(PREFIX + shortCode, link.getLongUrl()));
        return dbResult;
    }

    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
