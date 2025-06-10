package com.biblioteca.kafka.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.kafka.consumer.entities.Book;
import com.biblioteca.kafka.consumer.events.BookStoreEvent;
import com.biblioteca.kafka.consumer.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookStoreEventsService {

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    public void processBookStoreEvent(ConsumerRecord<Integer, String> record) throws Exception {
        BookStoreEvent event = objectMapper.readValue(record.value(), BookStoreEvent.class); // ele transforma o JSON em
                                                                                             // um objeto BookStoreEvent
        log.info("Evento recebido: {}", event);

        switch (event.getBookStoreEventType()) {
            case NEW:
                saveNew(event);
                break;
            case UPDATE:
                validate(event);
                saveUpdate(event);
                break;
            default:
                log.error("Evento desconhecido: {}", event.getBookStoreEventType());
        }
    }

    @Transactional
    protected void saveNew(BookStoreEvent event) {
        Book book = event.getBook();
        book.setBookStoreEvent(event);
        Book saved = bookRepository.save(book);
        log.info("Novo livro salvo: {}", saved);
    }

    @Transactional
    protected void saveUpdate(BookStoreEvent event) {
        Book book = event.getBook();
        book.setBookStoreEvent(event);
        Book updated = bookRepository.save(book);
        log.info("Livro atualizado: {}", updated);
    }

    private void validate(BookStoreEvent event) {
        if (event.getBook().getId() == null) {
            throw new IllegalArgumentException("ID do livro não pode ser nulo em UPDATE");
        }
    }
}
