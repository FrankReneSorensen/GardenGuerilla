package com.gardenguerilla.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.gardenguerilla.model.WeatherSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class MetNoClient {
    private static final Logger log = LoggerFactory.getLogger(MetNoClient.class);
    private final WebClient webClient;

    public MetNoClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Cacheable(value = "weatherCache", key = "#lat + ',' + #lon")
    public List<WeatherSnapshot> fetchForecast(double lat, double lon) {
        log.info("Fetching forecast from MET API for lat={}, lon={}", lat, lon);
        String url = "/compact?lat=" + lat + "&lon=" + lon;

        JsonNode root = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return parseTimeseries(root);
    }

    private List<WeatherSnapshot> parseTimeseries(JsonNode root) {
        List<WeatherSnapshot> snapshots = new ArrayList<>();
        JsonNode timeseries = root.path("properties").path("timeseries");

        for (JsonNode entry : timeseries) {
            try {
                Instant time = Instant.parse(entry.path("time").asText());
                JsonNode details = entry.path("data").path("instant").path("details");

                double temperature = details.path("air_temperature").asDouble(Double.NaN);
                double windSpeed = details.path("wind_speed").asDouble(Double.NaN);
                Double cloudAreaFraction = details.has("cloud_area_fraction")
                        ? details.path("cloud_area_fraction").asDouble() : null;
                Double relativeHumidity = details.has("relative_humidity")
                        ? details.path("relative_humidity").asDouble() : null;

                // Precipitation from next_1_hours or next_6_hours
                double precipitation = 0.0;
                JsonNode next1h = entry.path("data").path("next_1_hours").path("details");
                if (!next1h.isMissingNode()) {
                    precipitation = next1h.path("precipitation_amount").asDouble(0.0);
                } else {
                    JsonNode next6h = entry.path("data").path("next_6_hours").path("details");
                    if (!next6h.isMissingNode()) {
                        precipitation = next6h.path("precipitation_amount").asDouble(0.0) / 6.0;
                    }
                }

                if (!Double.isNaN(temperature)) {
                    snapshots.add(WeatherSnapshot.builder()
                            .time(time)
                            .temperature(temperature)
                            .precipitation(precipitation)
                            .windSpeed(windSpeed)
                            .cloudAreaFraction(cloudAreaFraction)
                            .relativeHumidity(relativeHumidity)
                            .build());
                }
            } catch (Exception e) {
                log.warn("Failed to parse timeseries entry: {}", e.getMessage());
            }
        }
        return snapshots;
    }
}
