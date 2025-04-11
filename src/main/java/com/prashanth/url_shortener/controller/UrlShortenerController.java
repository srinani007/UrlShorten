package com.prashanth.url_shortener.controller;

import com.prashanth.url_shortener.kafka.ClickLoggerProducer;
import com.prashanth.url_shortener.model.ShortLink;
import com.prashanth.url_shortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Tag(name = "URL Shortening")
@RestController
@RequestMapping("/api")
public class UrlShortenerController {


    @Autowired
    private UrlShortenerService service;

    @Autowired
    private ClickLoggerProducer clickLogger;

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody Map<String, String> body) {
        String longUrl = body.get("longUrl");
        if (longUrl == null || longUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing or invalid 'longUrl'");
        }

        String shortCode = service.shortenUrl(longUrl);
        return ResponseEntity.ok(Map.of("shortUrl", "http://localhost:8080/" + shortCode));
    }
    @GetMapping("/")
    public String home() {
        return "✅ URL Shortener is live! Use /shorten to POST and /{shortCode} to redirect.";
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<String> handle404() {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("🔍 Route not found. Try /shorten or /{code}");
        }
    }


    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<ShortLink> result = service.getOriginalUrl(shortCode);

        if (result.isPresent()) {
            ShortLink link = result.get();

            //Build click event
            String payload = String.format("{\"shortCode\":\"%s\", \"timestamp\":\"%s\", \"ip\":\"%s\", \"userAgent\":\"%s\"}",
                    shortCode,
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"));
            clickLogger.logClick(payload); // send to Kafka

            response.sendRedirect(link.getLongUrl());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Short link not found");
        }
    }

    @GetMapping("/error")
    public String handleError() {
        return "<h1>404 - Not Found</h1><p>Short link doesn’t exist.</p>";
    }



    @GetMapping("/shorten")
    public ResponseEntity<String> testPage() {
        return ResponseEntity.ok("Use POST to shorten your URL!");
    }

    @GetMapping("/ping")
    public String ping() {
        return "Controller is live!";
    }

}
