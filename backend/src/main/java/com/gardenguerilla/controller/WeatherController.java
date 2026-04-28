package com.gardenguerilla.controller;

import com.gardenguerilla.model.*;
import com.gardenguerilla.service.*;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class WeatherController {
    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;
    private final GardeningReadinessService readinessService;
    private final GardeningTipService tipService;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public WeatherController(WeatherService weatherService,
                              GardeningReadinessService readinessService,
                              GardeningTipService tipService) {
        this.weatherService = weatherService;
        this.readinessService = readinessService;
        this.tipService = tipService;
    }

    private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
            return Bucket.builder().addLimit(limit).build();
        });
    }

    @GetMapping("/gardening-readiness")
    public ResponseEntity<?> getGardeningReadiness(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp) {

        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid coordinates. lat must be -90 to 90, lon must be -180 to 180."));
        }

        String ipKey = clientIp != null ? clientIp : "default";
        Bucket bucket = getBucket(ipKey);
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for client: {}", ipKey);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many requests. Please wait a moment."));
        }

        try {
            log.info("Gardening readiness request for lat={}, lon={}", lat, lon);
            List<WeatherSnapshot> forecast = weatherService.getForecast(lat, lon);

            GardeningRecommendation recommendation = readinessService.evaluate(forecast);

            WeatherSnapshot current = forecast.isEmpty() ? null : forecast.get(0);

            List<WeatherSnapshot> forecast24h = forecast.stream()
                    .limit(24)
                    .toList();

            List<String> badges = buildBadges(current, forecast);

            GardeningResponse response = GardeningResponse.builder()
                    .recommendation(recommendation)
                    .currentWeather(current)
                    .forecast24h(forecast24h)
                    .badges(badges)
                    .guerillaTip(tipService.getDailyTip())
                    .attribution("Værdata levert av MET Norway / Yr (api.met.no). Fri bruk under NLOD / CC BY 4.0.")
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing gardening readiness request", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Klarte ikke hente værdata. Prøv igjen om litt."));
        }
    }

    private List<String> buildBadges(WeatherSnapshot current, List<WeatherSnapshot> forecast) {
        List<String> badges = new ArrayList<>();
        if (current == null) return badges;

        boolean rainComing = forecast.stream().limit(12)
                .anyMatch(s -> s.getPrecipitation() > 0.2);
        if (rainComing) badges.add("Regn på vei 🌧️");

        if (current.getWindSpeed() < 6) badges.add("Lav vind 💨");

        boolean frostRisk = forecast.stream().limit(24)
                .anyMatch(s -> s.getTemperature() < 2);
        if (frostRisk) badges.add("Frostfare ❄️");

        if (current.getTemperature() >= 8 && current.getTemperature() <= 20
                && current.getWindSpeed() < 6 && rainComing) {
            badges.add("Seed bomb ready 💣");
        }

        if (current.getCloudAreaFraction() != null && current.getCloudAreaFraction() < 50) {
            badges.add("Vent på skydekke ☁️");
        }

        return badges;
    }
}
