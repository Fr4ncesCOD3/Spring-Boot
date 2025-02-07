package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Prenotazione;

/**
 * Repository per la gestione delle operazioni di persistenza delle Prenotazioni.
 * Estende JpaRepository per ereditare le operazioni CRUD di base e altre funzionalità JPA.
 */
@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    
    /**
     * Verifica se esiste già una prenotazione per una specifica postazione in una data.
     * 
     * @param postazioneId ID della postazione da verificare
     * @param data data per cui verificare la disponibilità
     * @return true se esiste già una prenotazione, false altrimenti
     */
    boolean existsByPostazioneIdAndData(Long postazioneId, LocalDate data);
    
    /**
     * Verifica se un utente ha già una prenotazione per una specifica data.
     * 
     * @param utenteId ID dell'utente da verificare
     * @param data data per cui verificare le prenotazioni
     * @return true se l'utente ha già una prenotazione per quella data, false altrimenti
     */
    boolean existsByUtenteIdAndData(Long utenteId, LocalDate data);
    
    /**
     * Recupera tutte le prenotazioni effettuate da un utente specifico.
     * 
     * @param utenteId ID dell'utente di cui recuperare le prenotazioni
     * @return lista delle prenotazioni dell'utente
     */
    List<Prenotazione> findByUtenteId(Long utenteId);
    
    /**
     * Recupera tutte le prenotazioni per un edificio in una specifica data.
     * 
     * @param edificioId ID dell'edificio di cui recuperare le prenotazioni
     * @param data data per cui recuperare le prenotazioni
     * @return lista delle prenotazioni per l'edificio nella data specificata
     */
    List<Prenotazione> findByPostazioneEdificioIdAndData(Long edificioId, LocalDate data);
} 