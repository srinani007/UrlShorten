package com.prashanth.url_shortener.controller;

import com.prashanth.url_shortener.repository.ClickEventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/analytics-ui")
public class AnalyticsViewController {

    private final ClickEventRepository repository;

    public AnalyticsViewController(ClickEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{shortCode}")
    public String showAnalytics(@PathVariable String shortCode, Model model) {
        model.addAttribute("clicks", repository.findByShortCode(shortCode));
        model.addAttribute("shortCode", shortCode);
        System.out.println("✅ Reached analytics UI controller for " + shortCode);
        return "analytics"; // Name of HTML file in templates
    }
}
