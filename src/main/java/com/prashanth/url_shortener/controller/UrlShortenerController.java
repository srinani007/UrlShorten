package com.prashanth.url_shortener.controller;

import com.prashanth.url_shortener.model.ShortLink;
import com.prashanth.url_shortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class UrlShortenerController {


    @Autowired
    private UrlShortenerService service;

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
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        service.getOriginalUrl(shortCode).ifPresentOrElse(link -> {
            try {
                response.sendRedirect(link.getLongUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Short URL not found");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
