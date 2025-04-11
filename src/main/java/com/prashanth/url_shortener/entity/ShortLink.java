package com.prashanth.url_shortener.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ShortLink {
    @Schema(description = "The shortened code", example = "abc123")
    private String shortCode;

    @Schema(description = "The original long URL", example = "https://example.com/page")
    private String longUrl;
}
