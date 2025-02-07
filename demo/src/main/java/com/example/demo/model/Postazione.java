package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Postazione {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String codice;
    private String descrizione;
    
    @Enumerated(EnumType.STRING)
    private TipoPostazione tipo;
    
    private Integer numeroMassimoOccupanti;
    
    @JsonBackReference
    @ManyToOne
    private Edificio edificio;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "postazione")
    private List<Prenotazione> prenotazioni;
} 