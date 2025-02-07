package com.example.demo.repository;

import com.example.demo.model.Edificio;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per la gestione delle operazioni di persistenza degli Edifici.
 * Estende JpaRepository per ereditare le operazioni CRUD di base e altre funzionalità JPA.
 * 
 * @param <Edificio> l'entità gestita dal repository
 * @param <Long> il tipo dell'identificatore dell'entità
 */
public interface EdificioRepository extends JpaRepository<Edificio, Long> {
} 