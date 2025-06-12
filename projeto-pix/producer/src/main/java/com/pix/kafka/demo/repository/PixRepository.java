package com.pix.kafka.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pix.kafka.demo.domain.Pix;
@Repository
public interface PixRepository extends JpaRepository<Pix, Long> {

}
