package com.gardenguerilla.service;

import com.gardenguerilla.client.MetNoClient;
import com.gardenguerilla.model.WeatherSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {
    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    private final MetNoClient metNoClient;

    public WeatherService(MetNoClient metNoClient) {
        this.metNoClient = metNoClient;
    }

    public List<WeatherSnapshot> getForecast(double lat, double lon) {
        double roundedLat = Math.round(lat * 10000.0) / 10000.0;
        double roundedLon = Math.round(lon * 10000.0) / 10000.0;
        log.info("Getting forecast for lat={}, lon={}", roundedLat, roundedLon);
        return metNoClient.fetchForecast(roundedLat, roundedLon);
    }
}
