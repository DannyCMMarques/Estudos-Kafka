package com.biblioteca.kafka.consumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.biblioteca.kafka.consumer.service.BookStoreEventsService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookStoreEventConsumer {

    private final BookStoreEventsService bookStoreEventsService;

    @KafkaListener(topics = { "bookstore-events" }, groupId = "bookstore-group")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Mensagem recebida: {}", consumerRecord);

        try {
            bookStoreEventsService.processBookStoreEvent(consumerRecord);
        } catch (Exception e) {
            log.error("Erro ao processar evento: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar evento", e);
        }

    }
}
