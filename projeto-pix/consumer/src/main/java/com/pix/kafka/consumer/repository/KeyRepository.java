
package com.pix.kafka.consumer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pix.kafka.consumer.domain.Key;

@Repository
public interface KeyRepository extends JpaRepository<Key, Long> {
    Optional<Key> findByChave(String chave);

}
