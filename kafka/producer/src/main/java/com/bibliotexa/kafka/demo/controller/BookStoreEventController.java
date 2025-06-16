package com.bibliotexa.kafka.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotexa.kafka.demo.domain.BookStoreEventType;
import com.bibliotexa.kafka.demo.events.BookStoreEvent;
import com.bibliotexa.kafka.demo.producer.BookStoreEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/bookstore-events")
public class BookStoreEventController {
   

    @Autowired
    private BookStoreEventProducer bookStoreEventProducer;	

    @PostMapping
    public ResponseEntity<BookStoreEvent> postBookStoreEvent( @RequestBody  BookStoreEvent bookStoreEvent) {

        //um novo livro foi adicionado
        bookStoreEvent.setBookStoreEventType(BookStoreEventType.NEW);
        //dispara o evento
        try {
            bookStoreEventProducer.sendBookStoreEvent_Approach2(bookStoreEvent);
        } 
        catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookStoreEvent);
    }
}
