package com.pix.kafka.demo.service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pix.kafka.demo.DTO.PixDTO;
import com.pix.kafka.demo.domain.Pix;
import com.pix.kafka.demo.repository.PixRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PixService {
private final PixRepository pixRepository;
 private final KafkaTemplate<String, Pix> kafkaTemplate;

 public  PixDTO salvarPix(PixDTO pixDTO) {
 
    //TODO: mapStruct
    Pix pix = Pix.builder()
            .identifier(pixDTO.getIdentifier())
            .chaveOrigem(pixDTO.getChaveOrigem())
            .chaveDestino(pixDTO.getChaveDestino())
            .valor(pixDTO.getValor())
            .dataTransferencia(pixDTO.getDataTransferencia())
            .status(pixDTO.getStatus())
            .build();
     Pix savedPix = pixRepository.save(pix);
     kafkaTemplate.send("pix-topic", savedPix.getIdentifier(), savedPix);

     return pixDTO;
 }

}
