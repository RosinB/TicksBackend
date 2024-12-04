package com.example.demo.common.traffic;

import com.example.demo.util.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TrafficStatsService {

    @Autowired
    private RedisService redisService;

  
    public Map<String, Long> getTrafficStats() {
        String redisKey = "traffic:stats";
        Map<String, Long> stats = redisService.get(redisKey, new TypeReference<Map<String, Long>>() {});
        return stats != null ? stats : new HashMap<>();
    }
    
    
    public Map<Long, Long> getTrafficStatsPerSecond(long startTimestamp, long endTimestamp) {
        Map<Long, Long> stats = new HashMap<>();

        try {
            // 遍歷範圍內的所有鍵
            for (long ts = startTimestamp; ts <= endTimestamp; ts++) {
                String key = "traffic:stats:" + ts;
                Long count = redisService.get(key, Long.class);
                if (count != null) {
                    stats.put(ts, count);
                }
            }
        } catch (Exception e) {
            System.err.println("獲取每秒流量數據失敗：" + e.getMessage());
        }

        return stats;
    }
    
    
    
   
    public Map<String, Long> aggregateTrafficByRegion() {
        Map<String, Long> aggregatedStats = new HashMap<>();

        try {
            // 獲取所有匹配的鍵
            Set<String> keys = redisService.keys("traffic:stats:*");
            if (keys == null || keys.isEmpty()) {
                System.err.println("Redis 中沒有匹配的鍵！");
                return aggregatedStats;
            }

            // 批量獲取鍵值
            List<Object> values = redisService.multiGet(keys);
            if (values == null) {
                System.err.println("未能從 Redis 獲取數據！");
                return aggregatedStats;
            }

            // 遍歷鍵值對
            int index = 0;
            for (String key : keys) {
                String[] parts = key.split("_");
                if (parts.length < 4) {
                    System.err.println("鍵格式不正確，跳過：" + key);
                    continue;
                }
                String region = parts[3]; // 第四部分是區域

                // 獲取對應的值
                Long value = null;
                try {
                    value = Long.parseLong(values.get(index).toString());
                } catch (Exception e) {
                    System.err.println("數據解析錯誤：" + key + " -> " + values.get(index));
                    continue;
                }

                // 匯總區域數據
                aggregatedStats.merge(region, value, Long::sum);
                index++;
            }
        } catch (Exception e) {
            System.err.println("按區域分組時發生錯誤：" + e.getMessage());
        }

        return aggregatedStats;
    }


    /**
     * 按請求類型統計流量數據
     *
     * @return Map<String, Long> 按請求類型分組的流量數據
     */
    public Map<String, Long> aggregateTrafficByRequestType() {
        Map<String, Long> trafficStats = getTrafficStats();
        Map<String, Long> aggregatedStats = new HashMap<>();

        trafficStats.forEach((key, value) -> {
            // 分析鍵的結構：timestamp_requestType_region
            String[] parts = key.split("_");
            if (parts.length < 3) {
                return; // 確保鍵結構正確，避免數組越界
            }
            String requestType = parts[1]; // 第二部分是請求類型

            // 匯總請求類型數據
            aggregatedStats.merge(requestType, value, Long::sum);
        });

        return aggregatedStats;
    }
}