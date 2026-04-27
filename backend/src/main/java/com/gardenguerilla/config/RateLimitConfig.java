package com.gardenguerilla.config;

import org.springframework.context.annotation.Configuration;

/**
 * Rate limiting is handled per-request in WeatherController using Bucket4j.
 * This config class is reserved for any future global rate-limit tuning.
 */
@Configuration
public class RateLimitConfig {
}
