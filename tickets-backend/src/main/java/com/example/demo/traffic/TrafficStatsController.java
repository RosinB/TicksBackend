package com.example.demo.traffic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TrafficStatsController {

    @Autowired
    private TrafficStatsService trafficStatsService;

    @GetMapping("/api/traffic/region-stats")
    public Map<String, Long> getTrafficStatsByRegion() {
    	System.out.println(trafficStatsService.aggregateTrafficByRegion());
        return trafficStatsService.aggregateTrafficByRegion();
    }

    @GetMapping("/api/traffic/request-type-stats")
    public Map<String, Long> getTrafficStatsByRequestType() {
    	System.out.println("1234");
        return trafficStatsService.aggregateTrafficByRequestType();
    }
    
    @GetMapping("/api/traffic/per-second")
    public Map<Long, Long> getTrafficStatsPerSecond(
            @RequestParam("start") long startTimestamp,
            @RequestParam("end") long endTimestamp) {
        return trafficStatsService.getTrafficStatsPerSecond(startTimestamp, endTimestamp);
    }
}