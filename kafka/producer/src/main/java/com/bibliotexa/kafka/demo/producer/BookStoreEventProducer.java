package com.bibliotexa.kafka.demo.producer;

import com.bibliotexa.kafka.demo.events.BookStoreEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class BookStoreEventProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String topic = "bookstore-events";

    /**
     * Envia evento para o tópico Kafka de forma assíncrona usando o tópico default.
     */
    public void sendLibraryEvent(BookStoreEvent bookStoreEvent) throws JsonProcessingException {
        Integer key = bookStoreEvent.getBookStoreEventId();
        String value = objectMapper.writeValueAsString(bookStoreEvent);

        // Envia para o tópico padrão de forma assíncrona
        CompletableFuture<SendResult<Integer, String>> future = kafkaTemplate.sendDefault(key, value);

        // Define callbacks para sucesso e falha
        future.thenAccept(result -> handleSuccess(key, value, result))
                .exceptionally(ex -> {
                    handleFailure(key, value, ex);
                    return null;
                });
    }

    /**
     * Envia evento para o tópico Kafka criando manualmente o ProducerRecord com
     * headers.
     */
    public CompletableFuture<SendResult<Integer, String>> sendBookStoreEvent_Approach2(BookStoreEvent bookStoreEvent)
            throws JsonProcessingException {

        Integer key = bookStoreEvent.getBookStoreEventId();
        String value = objectMapper.writeValueAsString(bookStoreEvent);

        // Cria manualmente o registro Kafka com headers personalizados
        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);

        // Envia a mensagem e retorna CompletableFuture
        CompletableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(producerRecord);

        future.thenAccept(result -> handleSuccess(key, value, result))
                .exceptionally(ex -> {
                    handleFailure(key, value, ex);
                    return null;
                });

        return future;
    }

    /**
     * Cria manualmente um ProducerRecord com headers personalizados para
     * rastreamento.
     */
    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }

    /**
     * Envia evento de forma **síncrona**, aguardando até 1 segundo por resposta.
     * Útil em cenários onde você precisa garantir que a mensagem foi enviada.
     */
    public SendResult<Integer, String> sendBookStoreEventSynchronous(BookStoreEvent bookStoreEvent)
            throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        Integer key = bookStoreEvent.getBookStoreEventId();
        String value = objectMapper.writeValueAsString(bookStoreEvent);

        SendResult<Integer, String> sendResult;

        try {
            // Espera até 1 segundo pela confirmação do envio
            sendResult = kafkaTemplate.sendDefault(key, value).get(1, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Erro ao enviar mensagem (sync): {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar mensagem (sync): {}", e.getMessage());
            throw e;
        }

        return sendResult;
    }

    /**
     * Callback para tratar erro no envio de mensagem.
     */
    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Erro ao enviar mensagem. Key: {}, Value: {}, Erro: {}", key, value, ex.getMessage());
    }

    /**
     * Callback para tratar sucesso no envio de mensagem.
     */
    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Mensagem enviada com sucesso. Key: {}, Value: {}, Partition: {}",
                key, value, result.getRecordMetadata().partition());
    }
}
