package com.prashanth.url_shortener.controller;

import com.prashanth.url_shortener.entity.ClickEventEntity;
import com.prashanth.url_shortener.repository.ClickEventRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final ClickEventRepository repository;

    public AnalyticsController(ClickEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{shortCode}")
    public List<ClickEventEntity> getClicks(@PathVariable String shortCode) {
        return repository.findAll()
                .stream()
                .filter(click -> click.getShortCode().equals(shortCode))
                .toList();
    }
}
