package com.pix.kafka.demo.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pix.kafka.demo.service.PixService;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pix.kafka.demo.DTO.PixDTO;
import com.pix.kafka.demo.DTO.PixStatus;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixController {
    private final PixService pixService;

    @PostMapping
    public PixDTO salvarPix(@RequestBody PixDTO pixDTO) {

        pixDTO.setIdentifier(UUID.randomUUID().toString());
        pixDTO.setDataTransferencia(LocalDateTime.now());
        pixDTO.setStatus(PixStatus.EM_PROCESSAMENTO);

        return pixService.salvarPix(pixDTO);
    }

}
