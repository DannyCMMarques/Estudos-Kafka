package com.pix.kafka.consumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.pix.kafka.consumer.DTO.PixStatus;
import com.pix.kafka.consumer.domain.Key;
import com.pix.kafka.consumer.domain.Pix;
import com.pix.kafka.consumer.repository.KeyRepository;
import com.pix.kafka.consumer.repository.PixRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PixValidator {

        private final PixRepository pixRepository;
        private final KeyRepository keyRepository;

        @KafkaListener(topics = "pix-topic", groupId = "pix-group")
        public void validatePix(Pix dto) {
                log.info("Received PixDTO: {}", dto.getIdentifier());

                // 1) buscar a entidade Pix existente
                Pix pix = pixRepository.findByIdentifier(dto.getIdentifier())
                                .orElseThrow(() -> new RuntimeException("Pix not found: " + dto.getIdentifier()));

                // 2) verificar chaves
                Key origem = keyRepository.findByChave(pix.getChaveOrigem())
                                .orElseThrow(() -> new RuntimeException(PixStatus.ERRO + pix.getChaveOrigem()));

                Key destino = keyRepository.findByChave(pix.getChaveDestino())
                                .orElseThrow(() -> new RuntimeException(PixStatus.ERRO + pix.getChaveDestino()));

                // 3) processar e salvar
                pix.setStatus(PixStatus.PROCESSADO);
                pixRepository.save(pix);

                log.info("Pix processed successfully: {}", pix.getIdentifier());
                log.info("Chave Origem: {}", origem.getChave());
                log.info("Chave Destino: {}", destino.getChave());
        }
}
