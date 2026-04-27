package com.gardenguerilla.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GardeningScore {
    private int score;
    private String status;
    private String label;
    private String explanation;
}
