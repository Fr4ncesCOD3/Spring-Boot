package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

/**
 * Classe che rappresenta un utente nel sistema.
 * Gestisce le informazioni personali dell'utente e le sue prenotazioni.
 */
@Entity // Indica che questa classe è un'entità JPA
@Data // Lombok: genera automaticamente getter, setter, equals, hashCode e toString
@NoArgsConstructor // Lombok: genera un costruttore senza argomenti
public class Utente {
    
    /**
     * Identificatore univoco dell'utente
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Username univoco dell'utente utilizzato per l'identificazione
     */
    @Column(unique = true)
    private String username;
    
    /**
     * Nome e cognome dell'utente
     */
    private String nomeCompleto;
    
    /**
     * Indirizzo email univoco dell'utente
     */
    @Column(unique = true)
    private String email;
    
    /**
     * Lista delle prenotazioni effettuate dall'utente
     * JsonManagedReference gestisce la serializzazione bidirezionale
     * OneToMany indica la relazione uno-a-molti con le prenotazioni
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "utente")
    private List<Prenotazione> prenotazioni;
} 