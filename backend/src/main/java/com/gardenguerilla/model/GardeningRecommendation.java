package com.gardenguerilla.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class GardeningRecommendation {
    private int score;
    private String status;
    private String label;
    private String explanation;
    private Instant bestTimeStart;
    private long hoursUntilBestTime;
    private String missionModeText;
}
