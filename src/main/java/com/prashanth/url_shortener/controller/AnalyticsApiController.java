package com.prashanth.url_shortener.controller;

import com.prashanth.url_shortener.entity.ClickEventEntity;
import com.prashanth.url_shortener.entity.ShortenRequest;
import com.prashanth.url_shortener.repository.ClickEventRepository;
import com.prashanth.url_shortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Analytics")
@RestController
@RequestMapping("/analytics")
public class AnalyticsApiController {
    @Autowired
    private final ClickEventRepository repository;
    @Autowired
    private final UrlShortenerService urlShortenerService;

    public AnalyticsApiController(ClickEventRepository repository, UrlShortenerService urlShortenerService) {
        this.repository = repository;
        this.urlShortenerService = urlShortenerService;
    }


    @GetMapping("/{shortCode}/range")
    public List<ClickEventEntity> getAnalyticsInRange(
            @PathVariable String shortCode,
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
        LocalDateTime fromTime = LocalDateTime.parse(from);
        LocalDateTime toTime = LocalDateTime.parse(to);
        return repository.findByShortCodeAndTimestampBetween(shortCode, fromTime, toTime);
    }

    @GetMapping("/{shortCode}/paged")
    public Page<ClickEventEntity> getPagedAnalytics(
            @PathVariable String shortCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return repository.findByShortCode(shortCode, PageRequest.of(page, size));
    }

    @GetMapping("/{shortCode}/export")
    public void exportToCsv(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        List<ClickEventEntity> events = repository.findByShortCode(shortCode);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"analytics-" + shortCode + ".csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Timestamp,IP,UserAgent");
        for (ClickEventEntity event : events) {
            writer.println(event.getTimestamp() + "," + event.getIp() + "," + event.getUserAgent());
        }
        writer.flush();
        writer.close();
    }

    @Operation(summary = "Get click stats for shortCode")
    @GetMapping("/{shortCode}/summary")
    public Map<String, Object> getAnalyticsSummary(@PathVariable String shortCode) {
        List<ClickEventEntity> clicks = repository.findByShortCode(shortCode);

        Set<String> uniqueIps = clicks.stream().map(ClickEventEntity::getIp).collect(Collectors.toSet());
        long totalClicks = clicks.size();
        Optional<LocalDateTime> firstClick = clicks.stream().map(ClickEventEntity::getTimestamp).min(Comparator.naturalOrder());
        Optional<LocalDateTime> lastClick = clicks.stream().map(ClickEventEntity::getTimestamp).max(Comparator.naturalOrder());

        return Map.of(
                "shortCode", shortCode,
                "totalClicks", totalClicks,
                "uniqueIps", uniqueIps.size(),
                "firstClick", firstClick.orElse(null),
                "lastClick", lastClick.orElse(null)
        );
    }

    @Operation(
            summary = "Shorten a long URL",
            description = "Takes a long URL and returns a shortened version. The short code is stored and cached."
    )
    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody ShortenRequest request) {
        return urlShortenerService.shortenUrl(String.valueOf(request));
    }





}

