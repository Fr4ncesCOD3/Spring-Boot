package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

/**
 * Classe che rappresenta una postazione di lavoro nel sistema.
 * Può essere di tipo privato, open space o sala riunioni.
 */
@Entity // Indica che questa classe è un'entità JPA
@Data // Lombok: genera automaticamente getter, setter, equals, hashCode e toString
@NoArgsConstructor // Lombok: genera un costruttore senza argomenti
public class Postazione {
    
    /**
     * Identificatore univoco della postazione
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Codice identificativo della postazione (es. MI001, RM002)
     */
    private String codice;

    /**
     * Descrizione dettagliata della postazione
     */
    private String descrizione;
    
    /**
     * Tipo della postazione (PRIVATO, OPENSPACE, SALA_RIUNIONI)
     */
    @Enumerated(EnumType.STRING)
    private TipoPostazione tipo;
    
    /**
     * Numero massimo di persone che possono occupare la postazione
     */
    private Integer numeroMassimoOccupanti;
    
    /**
     * Edificio in cui si trova la postazione
     * JsonBackReference gestisce la serializzazione bidirezionale evitando cicli infiniti
     */
    @JsonBackReference
    @ManyToOne
    private Edificio edificio;
    
    /**
     * Lista delle prenotazioni associate a questa postazione
     * JsonManagedReference gestisce la serializzazione bidirezionale
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "postazione")
    private List<Prenotazione> prenotazioni;
} 