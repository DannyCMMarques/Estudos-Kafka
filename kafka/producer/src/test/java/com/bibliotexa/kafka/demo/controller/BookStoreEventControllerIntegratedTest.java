package com.bibliotexa.kafka.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.bibliotexa.kafka.demo.domain.Book;
import com.bibliotexa.kafka.demo.events.BookStoreEvent;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = "bookstore-events", partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})

public class BookStoreEventControllerIntegratedTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    private Consumer<Integer, String> consumer;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(
                KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));

        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer())
                .createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "bookstore-events");

    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void deverPostarBookStoreEvent() {
        // Given
        Book book = Book.builder()
                .id(1L)
                .title("Livro de Teste")
                .author("Autor Teste")
                .build();
        BookStoreEvent bookStoreEvent = BookStoreEvent.builder()
                .bookStoreEventId(null)
                .book(book)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookStoreEvent> request = new HttpEntity<>(bookStoreEvent, headers);

        // when
        ResponseEntity<BookStoreEvent> responseEntity = testRestTemplate.exchange("/api/v1/bookstore-events",
                HttpMethod.POST, request, BookStoreEvent.class);

        // then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ConsumerRecord<Integer, String> record = KafkaTestUtils.getSingleRecord(consumer, "bookstore-events");
        String expectedJson = "{\"bookStoreEventId\":null," +
                "\"book\":{\"id\":1,\"title\":\"Livro de Teste\",\"author\":\"Autor Teste\"}," +
                "\"bookStoreEventType\":\"NEW\"}";

        assertEquals(expectedJson, record.value());

  
    }
}
