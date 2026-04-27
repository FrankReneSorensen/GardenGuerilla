package com.gardenguerilla.model;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherSnapshot {
    private Instant time;
    private double temperature;       // Celsius
    private double precipitation;     // mm next 1h
    private double windSpeed;         // m/s
    private Double cloudAreaFraction; // % 0-100, nullable
    private Double relativeHumidity;  // % 0-100, nullable
}
