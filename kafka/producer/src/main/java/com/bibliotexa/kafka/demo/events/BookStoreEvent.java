package com.bibliotexa.kafka.demo.events;

import com.bibliotexa.kafka.demo.domain.Book;
import com.bibliotexa.kafka.demo.domain.BookStoreEventType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookStoreEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookStoreEventId;
    private Book book;
    @Enumerated(EnumType.STRING)
    private BookStoreEventType bookStoreEventType;
}
