package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Utente;
import com.example.demo.repository.UtenteRepository;

/**
 * Service che gestisce la logica di business per gli utenti.
 * Fornisce metodi per la registrazione, ricerca ed eliminazione degli utenti.
 */
@Service
public class UtenteService {
    
    @Autowired
    private UtenteRepository utenteRepository;
    
    /**
     * Verifica se esiste già un utente con lo username specificato.
     * 
     * @param username lo username da verificare
     * @return true se esiste già un utente con questo username, false altrimenti
     */
    public boolean existsByUsername(String username) {
        return utenteRepository.existsByUsername(username);
    }
    
    /**
     * Registra un nuovo utente nel sistema.
     * Verifica che username ed email non siano già in uso.
     * 
     * @param username lo username del nuovo utente
     * @param nomeCompleto il nome completo del nuovo utente
     * @param email l'email del nuovo utente
     * @return l'utente registrato
     * @throws IllegalStateException se username o email sono già in uso
     */
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
    
    /**
     * Recupera tutti gli utenti registrati nel sistema.
     * 
     * @return lista di tutti gli utenti
     */
    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }
    
    /**
     * Elimina un utente dal sistema tramite il suo username.
     * 
     * @param username lo username dell'utente da eliminare
     * @throws IllegalArgumentException se l'utente non viene trovato
     */
    public void deleteUtente(String username) {
        Utente utente = utenteRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        utenteRepository.delete(utente);
    }
} 