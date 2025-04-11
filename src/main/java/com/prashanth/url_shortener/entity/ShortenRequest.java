package com.prashanth.url_shortener.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ShortenRequest {
    @Schema(description = "The original long URL", example = "https://example.com/page")
    private String longUrl;
}
