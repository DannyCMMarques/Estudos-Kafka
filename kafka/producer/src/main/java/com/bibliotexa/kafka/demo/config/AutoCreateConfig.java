package com.bibliotexa.kafka.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;

@Configuration
public class AutoCreateConfig {
// assim se cria o tópico automaticamente somente para fins de teste
    @Bean 
    public NewTopic bookStoreEventTopic() {

        return TopicBuilder.name("bookstore-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
