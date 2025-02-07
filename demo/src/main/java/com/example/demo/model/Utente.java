package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Utente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;
    
    private String nomeCompleto;
    
    @Column(unique = true)
    private String email;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "utente")
    private List<Prenotazione> prenotazioni;
} 