package com.gardenguerilla;

import com.gardenguerilla.model.WeatherSnapshot;
import com.gardenguerilla.service.GardeningReadinessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GardeningReadinessServiceTest {
    private GardeningReadinessService service;

    @BeforeEach
    void setUp() {
        service = new GardeningReadinessService();
    }

    private WeatherSnapshot snapshot(double temp, double wind, Double cloud, Double humidity) {
        return WeatherSnapshot.builder()
                .time(Instant.now())
                .temperature(temp)
                .precipitation(0.0)
                .windSpeed(wind)
                .cloudAreaFraction(cloud)
                .relativeHumidity(humidity)
                .build();
    }

    @Test
    void perfectConditions_shouldScoreHigh() {
        WeatherSnapshot snap = snapshot(15.0, 3.0, 70.0, 70.0);
        int score = service.calculateScore(snap, 1.5, 5.0);
        // +30 temp, +20 precip, +15 wind, +15 no frost, +10 cloud, +10 humidity = 100
        assertThat(score).isEqualTo(100);
    }

    @Test
    void freezingTemperature_shouldReduceScore() {
        WeatherSnapshot snap = snapshot(-5.0, 3.0, null, null);
        int score = service.calculateScore(snap, 0.0, -5.0);
        // -30 temp
        assertThat(score).isLessThan(0);
    }

    @Test
    void highWind_shouldReduceScore() {
        WeatherSnapshot snap = snapshot(15.0, 12.0, null, null);
        int score = service.calculateScore(snap, 1.5, 5.0);
        // +30 temp, +20 precip, -20 wind, +15 no frost = 45
        assertThat(score).isEqualTo(45);
    }

    @Test
    void heavyRain_shouldReduceScore() {
        WeatherSnapshot snap = snapshot(15.0, 3.0, null, null);
        int score = service.calculateScore(snap, 15.0, 5.0);
        // +30 temp, -25 heavy rain, +15 wind, +15 no frost = 35
        assertThat(score).isEqualTo(35);
    }

    @Test
    void hotAndDry_shouldReduceScore() {
        WeatherSnapshot snap = snapshot(25.0, 3.0, null, null);
        int score = service.calculateScore(snap, 0.0, 15.0);
        // -20 no rain and hot, +15 wind, +15 no frost = 10
        assertThat(score).isEqualTo(10);
    }

    @Test
    void scoreIsClamped_between0and100() {
        WeatherSnapshot snap = snapshot(15.0, 3.0, 70.0, 70.0);
        int score = service.calculateScore(snap, 1.5, 5.0);
        assertThat(score).isBetween(0, 100);
    }

    @Test
    void evaluate_shouldReturnValidRecommendation() {
        List<WeatherSnapshot> list = List.of(
            WeatherSnapshot.builder()
                    .time(Instant.now())
                    .temperature(15.0).precipitation(0.5).windSpeed(3.0)
                    .cloudAreaFraction(70.0).relativeHumidity(70.0).build(),
            WeatherSnapshot.builder()
                    .time(Instant.now().plusSeconds(3600))
                    .temperature(14.0).precipitation(0.3).windSpeed(2.5)
                    .cloudAreaFraction(65.0).relativeHumidity(65.0).build()
        );
        var rec = service.evaluate(list);
        assertThat(rec.getScore()).isGreaterThanOrEqualTo(0);
        assertThat(rec.getLabel()).isNotBlank();
        assertThat(rec.getMissionModeText()).isNotBlank();
    }
}
