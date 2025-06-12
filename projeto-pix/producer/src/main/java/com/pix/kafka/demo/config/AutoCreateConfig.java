package com.pix.kafka.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateConfig {
    @Bean 
    public NewTopic pixTopic() {

        return TopicBuilder.name("pix-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
