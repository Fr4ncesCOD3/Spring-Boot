package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDate;

/**
 * Classe che rappresenta una prenotazione nel sistema.
 * Gestisce l'associazione tra un utente e una postazione per una data specifica.
 */
@Entity // Indica che questa classe è un'entità JPA
@Data // Lombok: genera automaticamente getter, setter, equals, hashCode e toString
@NoArgsConstructor // Lombok: genera un costruttore senza argomenti
public class Prenotazione {
    
    /**
     * Identificatore univoco della prenotazione
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Utente che ha effettuato la prenotazione
     * JsonBackReference gestisce la serializzazione bidirezionale evitando cicli infiniti
     */
    @JsonBackReference
    @ManyToOne
    private Utente utente;
    
    /**
     * Postazione prenotata
     * JsonBackReference gestisce la serializzazione bidirezionale evitando cicli infiniti
     */
    @JsonBackReference
    @ManyToOne
    private Postazione postazione;
    
    /**
     * Data per la quale è stata effettuata la prenotazione
     */
    private LocalDate data;
} 