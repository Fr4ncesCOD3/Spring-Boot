package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Utente;

/**
 * Repository per la gestione delle operazioni di persistenza degli Utenti.
 * Estende JpaRepository per ereditare le operazioni CRUD di base e altre funzionalità JPA.
 */
public interface UtenteRepository extends JpaRepository<Utente, Long> {
    
    /**
     * Cerca un utente tramite il suo username.
     * 
     * @param username lo username dell'utente da cercare
     * @return Optional contenente l'utente se trovato
     */
    Optional<Utente> findByUsername(String username);
    
    /**
     * Verifica se esiste già un utente con la email specificata.
     * 
     * @param email l'email da verificare
     * @return true se esiste già un utente con questa email, false altrimenti
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica se esiste già un utente con lo username specificato.
     * 
     * @param username lo username da verificare
     * @return true se esiste già un utente con questo username, false altrimenti
     */
    boolean existsByUsername(String username);
} 