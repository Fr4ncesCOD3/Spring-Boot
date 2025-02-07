package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PrenotazioneRequest;
import com.example.demo.model.Postazione;
import com.example.demo.model.Prenotazione;
import com.example.demo.model.TipoPostazione;
import com.example.demo.service.PrenotazioneService;

/**
 * Controller che gestisce le operazioni relative alle prenotazioni delle postazioni.
 * Espone endpoint REST per la gestione delle prenotazioni.
 */
@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {
    
    /**
     * Service che implementa la logica di business per le prenotazioni
     */
    @Autowired
    private PrenotazioneService prenotazioneService;
    
    /**
     * Endpoint per effettuare una nuova prenotazione
     * @param request oggetto contenente username, codice postazione e data della prenotazione
     * @return la prenotazione creata in caso di successo, messaggio di errore altrimenti
     */
    @PostMapping("/prenota")
    public ResponseEntity<?> prenotaPostazione(@RequestBody PrenotazioneRequest request) {
        try {
            Prenotazione prenotazione = prenotazioneService.prenotaPostazioneByUsername(
                request.getUsername(), 
                request.getCodicePostazione(), 
                request.getData()
            );
            return ResponseEntity.ok(prenotazione);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Ricerca le postazioni disponibili in base ai criteri specificati
     * @param tipo il tipo di postazione richiesto (PRIVATO, OPENSPACE, SALA_RIUNIONI)
     * @param citta la città dove cercare le postazioni
     * @param data la data per cui verificare la disponibilità
     * @return lista delle postazioni disponibili che soddisfano i criteri
     */
    @GetMapping("/disponibili")
    public ResponseEntity<List<Postazione>> cercaPostazioniDisponibili(
            @RequestParam TipoPostazione tipo,
            @RequestParam String citta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<Postazione> postazioni = prenotazioneService.cercaPostazioniDisponibili(tipo, citta, data);
        return ResponseEntity.ok(postazioni);
    }
    
    /**
     * Recupera tutte le prenotazioni effettuate da un utente
     * @param username username dell'utente di cui recuperare le prenotazioni
     * @return lista delle prenotazioni dell'utente specificato
     */
    @GetMapping("/utente/{username}")
    public ResponseEntity<List<Prenotazione>> getPrenotazioniUtente(@PathVariable String username) {
        List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniByUsername(username);
        return ResponseEntity.ok(prenotazioni);
    }
} 