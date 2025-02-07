package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Postazione;
import com.example.demo.model.TipoPostazione;

/**
 * Repository per la gestione delle operazioni di persistenza delle Postazioni.
 * Estende JpaRepository per ereditare le operazioni CRUD di base e altre funzionalità JPA.
 */
@Repository
public interface PostazioneRepository extends JpaRepository<Postazione, Long> {
    
    /**
     * Trova tutte le postazioni di un determinato tipo in una specifica città.
     * Utilizza una query JPQL personalizzata per la ricerca.
     * 
     * @param tipo il tipo di postazione da cercare
     * @param citta la città in cui cercare le postazioni
     * @return lista delle postazioni che corrispondono ai criteri
     */
    @Query("SELECT p FROM Postazione p WHERE p.tipo = :tipo AND p.edificio.citta = :citta")
    List<Postazione> findByTipoAndCitta(TipoPostazione tipo, String citta);

    /**
     * Trova una postazione tramite il suo codice univoco.
     * 
     * @param codice il codice della postazione da cercare
     * @return Optional contenente la postazione se trovata
     */
    Optional<Postazione> findByCodice(String codice);

    /**
     * Trova tutte le postazioni presenti in un determinato edificio.
     * 
     * @param edificioId l'ID dell'edificio di cui recuperare le postazioni
     * @return lista delle postazioni presenti nell'edificio
     */
    List<Postazione> findByEdificioId(Long edificioId);
} 