package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Prenotazione;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    
    boolean existsByPostazioneIdAndData(Long postazioneId, LocalDate data);
    
    boolean existsByUtenteIdAndData(Long utenteId, LocalDate data);
    
    List<Prenotazione> findByUtenteId(Long utenteId);
    
    List<Prenotazione> findByPostazioneEdificioIdAndData(Long edificioId, LocalDate data);
} 