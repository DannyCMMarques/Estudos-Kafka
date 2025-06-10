package com.biblioteca.kafka.consumer.events;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.biblioteca.kafka.consumer.entities.Book;
import jakarta.persistence.OneToOne;
import lombok.ToString;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class BookStoreEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookStoreEventId;

    @Enumerated(EnumType.STRING)
    private BookStoreEventType bookStoreEventType;

    @OneToOne(mappedBy = "bookStoreEvent")
    @ToString.Exclude
    private Book book;
}
