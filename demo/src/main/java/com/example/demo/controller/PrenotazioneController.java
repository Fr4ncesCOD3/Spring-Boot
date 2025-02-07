package com.example.demo.controller;

import com.example.demo.model.Postazione;
import com.example.demo.model.Prenotazione;
import com.example.demo.model.TipoPostazione;
import com.example.demo.service.PrenotazioneService;
import com.example.demo.dto.PrenotazioneRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {
    
    @Autowired
    private PrenotazioneService prenotazioneService;
    
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
    
    @GetMapping("/disponibili")
    public ResponseEntity<List<Postazione>> cercaPostazioniDisponibili(
            @RequestParam TipoPostazione tipo,
            @RequestParam String citta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<Postazione> postazioni = prenotazioneService.cercaPostazioniDisponibili(tipo, citta, data);
        return ResponseEntity.ok(postazioni);
    }
    
    @GetMapping("/utente/{username}")
    public ResponseEntity<List<Prenotazione>> getPrenotazioniUtente(@PathVariable String username) {
        List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniByUsername(username);
        return ResponseEntity.ok(prenotazioni);
    }
} 