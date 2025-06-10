package com.biblioteca.kafka.consumer.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.kafka.consumer.entities.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
