package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Prenotazione {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonBackReference
    @ManyToOne
    private Utente utente;
    
    @JsonBackReference
    @ManyToOne
    private Postazione postazione;
    
    private LocalDate data;
} 