package com.pix.kafka.consumer.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pix.kafka.consumer.domain.Pix;
@Repository
public interface PixRepository extends JpaRepository<Pix, Long> {
 Optional<Pix> findByIdentifier(String identifier);
}
