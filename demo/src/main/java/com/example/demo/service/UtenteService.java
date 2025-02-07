package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Utente;
import com.example.demo.repository.UtenteRepository;

@Service
public class UtenteService {
    
    @Autowired
    private UtenteRepository utenteRepository;
    
    public boolean existsByUsername(String username) {
        return utenteRepository.existsByUsername(username);
    }
    
    public Utente registraUtente(String username, String nomeCompleto, String email) {
        if (utenteRepository.existsByUsername(username)) {
            throw new IllegalStateException("Username già in uso");
        }
        if (utenteRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email già in uso");
        }
        
        Utente utente = new Utente();
        utente.setUsername(username);
        utente.setNomeCompleto(nomeCompleto);
        utente.setEmail(email);
        
        return utenteRepository.save(utente);
    }
    
    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }
    
    public void deleteUtente(String username) {
        Utente utente = utenteRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        utenteRepository.delete(utente);
    }
} 