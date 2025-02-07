package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PrenotazioneRequest {
    private String username;
    private String codicePostazione;
    private LocalDate data;
} 