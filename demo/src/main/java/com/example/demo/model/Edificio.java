package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

/**
 * Classe che rappresenta un edificio nel sistema.
 * Contiene le informazioni di base di un edificio e la lista delle postazioni in esso contenute.
 */
@Entity // Indica che questa classe è un'entità JPA
@Data // Lombok: genera automaticamente getter, setter, equals, hashCode e toString
@NoArgsConstructor // Lombok: genera un costruttore senza argomenti
public class Edificio {
    
    /**
     * Identificatore univoco dell'edificio
     */
    @Id // Indica che questo campo è la chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera automaticamente l'ID
    private Long id;
    
    /**
     * Nome dell'edificio
     */
    private String nome;

    /**
     * Indirizzo fisico dell'edificio
     */
    private String indirizzo;

    /**
     * Città in cui si trova l'edificio
     */
    private String citta;
    
    /**
     * Lista delle postazioni presenti nell'edificio
     * JsonManagedReference gestisce la serializzazione bidirezionale
     * OneToMany indica la relazione uno-a-molti con le postazioni
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "edificio")
    private List<Postazione> postazioni;
} 