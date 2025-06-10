package com.biblioteca.kafka.consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.kafka.consumer.events.BookStoreEvent;

@Repository
public interface BookStoreEventRepository extends JpaRepository<BookStoreEvent, Integer> {

}
