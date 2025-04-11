package com.prashanth.url_shortener.service;

import com.prashanth.url_shortener.model.ShortLink;
import com.prashanth.url_shortener.repository.ShortLinkRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UrlShortenerService {

    private final ShortLinkRepository repository;

    public UrlShortenerService(ShortLinkRepository repository) {
        this.repository = repository;
    }

    // Method to generate a short link
    public String shortenUrl(String longUrl) {
        // Generate a unique short code
        String shortCode = generateShortCode();
        ShortLink link = new ShortLink();
        link.setShortCode(shortCode);
        link.setLongUrl(longUrl);
        repository.save(link);
        return shortCode;
    }

    public Optional<ShortLink> getOriginalUrl(String shortcode){
        return repository.findByShortCode(shortcode);
    }

    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            shortCode.append(chars.charAt(random.nextInt(chars.length())));
        }
        return shortCode.toString();
    }
}
