package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	  // 搶票隊列
    public static final String TICKET_QUEUE_NAME = "ticket_sales_queue";
    public static final String TICKET_ROUTING_KEY = "ticket.sales.key";

    // 流量監控隊列
    public static final String TRAFFIC_QUEUE_NAME = "traffic_monitor_queue";
    public static final String TRAFFIC_ROUTING_KEY = "traffic.monitor.key";
    
    // 庫存更新隊列
    public static final String STOCK_UPDATE_QUEUE_NAME = "stock_update_queue";
    public static final String STOCK_UPDATE_ROUTING_KEY = "stock.update.key";
    
    
    
    
    public static final String EXCHANGE_NAME = "app_exchange";
    
    
    // 定義搶票隊列
    @Bean
    public Queue ticketQueue() {
        return new Queue(TICKET_QUEUE_NAME, true);
    }

    // 定義流量監控隊列
    @Bean
    public Queue trafficQueue() {
        return new Queue(TRAFFIC_QUEUE_NAME, true);
    }
    
    
    // 定義庫存更新隊列
    @Bean
    public Queue stockUpdateQueue() {
        return new Queue(STOCK_UPDATE_QUEUE_NAME, true);
    }
    
    
    // 定義通用交換機
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    
    // 綁定搶票隊列和交換機
    @Bean
    public Binding ticketBinding(@Qualifier("ticketQueue") Queue ticketQueue, TopicExchange exchange) {
        return BindingBuilder.bind(ticketQueue).to(exchange).with(TICKET_ROUTING_KEY);
    }

    // 綁定流量監控隊列和交換機
    @Bean
    public Binding trafficBinding(@Qualifier("trafficQueue") Queue trafficQueue, TopicExchange exchange) {
        return BindingBuilder.bind(trafficQueue).to(exchange).with(TRAFFIC_ROUTING_KEY);
    }
    
    @Bean
    public Binding stockUpdateBinding(@Qualifier("stockUpdateQueue") Queue stockUpdateQueue, TopicExchange exchange) {
        return BindingBuilder.bind(stockUpdateQueue).to(exchange).with(STOCK_UPDATE_ROUTING_KEY);
    }
    
    

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    
}
