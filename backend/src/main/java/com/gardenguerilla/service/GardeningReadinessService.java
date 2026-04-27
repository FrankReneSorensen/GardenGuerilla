package com.gardenguerilla.service;

import com.gardenguerilla.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class GardeningReadinessService {
    private static final Logger log = LoggerFactory.getLogger(GardeningReadinessService.class);
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    public GardeningRecommendation evaluate(List<WeatherSnapshot> forecast) {
        if (forecast == null || forecast.isEmpty()) {
            throw new IllegalArgumentException("Empty forecast data");
        }

        WeatherSnapshot current = forecast.get(0);
        Instant now = Instant.now();
        Instant in12h = now.plus(12, ChronoUnit.HOURS);
        Instant in24h = now.plus(24, ChronoUnit.HOURS);

        List<WeatherSnapshot> next12h = forecast.stream()
                .filter(s -> !s.getTime().isBefore(now) && s.getTime().isBefore(in12h))
                .toList();
        List<WeatherSnapshot> next24h = forecast.stream()
                .filter(s -> !s.getTime().isBefore(now) && s.getTime().isBefore(in24h))
                .toList();

        double totalPrecip12h = next12h.stream().mapToDouble(WeatherSnapshot::getPrecipitation).sum();
        double minTemp24h = next24h.stream().mapToDouble(WeatherSnapshot::getTemperature)
                .min().orElse(current.getTemperature());

        int score = calculateScore(current, totalPrecip12h, minTemp24h);
        score = Math.max(0, Math.min(100, score));

        String status = getStatus(score);
        String label = getLabel(score);
        String explanation = buildExplanation(current, totalPrecip12h, minTemp24h, score);

        Instant in48h = now.plus(48, ChronoUnit.HOURS);
        List<WeatherSnapshot> next48h = forecast.stream()
                .filter(s -> !s.getTime().isBefore(now) && s.getTime().isBefore(in48h))
                .toList();

        Optional<WeatherSnapshot> bestSlot = findBestSlot(next48h);
        Instant bestTime = bestSlot.map(WeatherSnapshot::getTime).orElse(now);
        long hoursUntil = ChronoUnit.HOURS.between(now, bestTime);

        String missionMode = hoursUntil <= 1
                ? "Det grønne vinduet er ÅPENT nå! 🌿"
                : "Neste grønne vindu åpner om " + hoursUntil + " timer.";

        auditLog.info("Gardening readiness evaluated: score={}, status={}, lat/lon=REDACTED", score, status);

        return GardeningRecommendation.builder()
                .score(score)
                .status(status)
                .label(label)
                .explanation(explanation)
                .bestTimeStart(bestTime)
                .hoursUntilBestTime(Math.max(0, hoursUntil))
                .missionModeText(missionMode)
                .build();
    }

    public int calculateScore(WeatherSnapshot current, double totalPrecip12h, double minTemp24h) {
        int score = 0;

        // Temperature scoring
        if (current.getTemperature() >= 8 && current.getTemperature() <= 20) {
            score += 30;
        } else if (current.getTemperature() < 0) {
            score -= 30;
        }

        // Precipitation scoring
        if (totalPrecip12h >= 0.2 && totalPrecip12h <= 5.0) {
            score += 20;
        } else if (totalPrecip12h > 10.0) {
            score -= 25;
        } else if (totalPrecip12h == 0 && current.getTemperature() > 22) {
            score -= 20;
        }

        // Wind scoring
        if (current.getWindSpeed() < 6.0) {
            score += 15;
        } else if (current.getWindSpeed() > 10.0) {
            score -= 20;
        }

        // Frost risk
        if (minTemp24h >= 2.0) {
            score += 15;
        }

        // Cloud cover
        if (current.getCloudAreaFraction() != null && current.getCloudAreaFraction() > 50) {
            score += 10;
        }

        // Humidity
        if (current.getRelativeHumidity() != null && current.getRelativeHumidity() > 60) {
            score += 10;
        }

        return score;
    }

    private Optional<WeatherSnapshot> findBestSlot(List<WeatherSnapshot> snapshots) {
        return snapshots.stream()
                .max(Comparator.comparingInt(s -> {
                    double precip = snapshots.stream()
                            .filter(x -> !x.getTime().isBefore(s.getTime())
                                    && x.getTime().isBefore(s.getTime().plus(12, ChronoUnit.HOURS)))
                            .mapToDouble(WeatherSnapshot::getPrecipitation)
                            .sum();
                    double minT = snapshots.stream()
                            .filter(x -> !x.getTime().isBefore(s.getTime())
                                    && x.getTime().isBefore(s.getTime().plus(24, ChronoUnit.HOURS)))
                            .mapToDouble(WeatherSnapshot::getTemperature)
                            .min().orElse(s.getTemperature());
                    return calculateScore(s, precip, minT);
                }));
    }

    private String getStatus(int score) {
        if (score >= 80) return "PERFECT";
        if (score >= 60) return "GOOD";
        if (score >= 40) return "POSSIBLE";
        return "WAIT";
    }

    private String getLabel(int score) {
        if (score >= 80) return "Perfekt kveld for planting 🌱";
        if (score >= 60) return "Gjør deg klar – gode forhold snart";
        if (score >= 40) return "Mulig, men ikke optimalt";
        return "Vent litt";
    }

    private String buildExplanation(WeatherSnapshot current, double totalPrecip12h,
                                    double minTemp24h, int score) {
        List<String> parts = new ArrayList<>();
        parts.add(String.format("Temperatur: %.1f°C", current.getTemperature()));
        parts.add(String.format("Nedbør neste 12t: %.1f mm", totalPrecip12h));
        parts.add(String.format("Vind: %.1f m/s", current.getWindSpeed()));
        if (minTemp24h < 2) parts.add("⚠️ Frostfare neste 24t!");
        if (totalPrecip12h >= 0.2 && totalPrecip12h <= 5) parts.add("✅ Lett regn – perfekt for spirer");
        if (current.getWindSpeed() < 6) parts.add("✅ Lav vind");
        return String.join(". ", parts) + ".";
    }
}
