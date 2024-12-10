package com.example.demo.common.consumer;





import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Service;

import com.example.demo.adminPanel.dto.traffic.TrafficDto;
import com.example.demo.adminPanel.service.common.TrafficWatcherService;
import com.example.demo.common.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficConsumer {

	private final  TrafficWatcherService trafficWatcherService;
	
    
    @RabbitListener(queues = RabbitMQConfig.TRAFFIC_QUEUE_NAME)
    public void processTrafficData(TrafficDto trafficData) {
    	trafficWatcherService.recordUserBehavior(trafficData);

    }
    
}