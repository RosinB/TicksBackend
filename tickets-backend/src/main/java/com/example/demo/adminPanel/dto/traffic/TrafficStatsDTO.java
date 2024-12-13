package com.example.demo.adminPanel.dto.traffic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrafficStatsDTO {
    private String type;
    private long totalTraffic;
    private int qps;
    private long timestamp;
}