package com.gardenguerilla.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GardeningResponse {
    private GardeningRecommendation recommendation;
    private WeatherSnapshot currentWeather;
    private List<WeatherSnapshot> forecast24h;
    private List<String> badges;
    private String guerillaTip;
    private String attribution;
}
