package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * Classe DTO (Data Transfer Object) che rappresenta una richiesta di prenotazione.
 * Utilizzata per trasferire i dati della prenotazione dal client al server.
 */
@Data
public class PrenotazioneRequest {
    /**
     * Username dell'utente che effettua la prenotazione
     */
    private String username;
    
    /**
     * Codice identificativo della postazione da prenotare
     */
    private String codicePostazione;
    
    /**
     * Data per la quale si richiede la prenotazione
     */
    private LocalDate data;
} 