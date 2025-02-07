package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Edificio;
import com.example.demo.model.Postazione;
import com.example.demo.model.Prenotazione;
import com.example.demo.model.TipoPostazione;
import com.example.demo.model.Utente;
import com.example.demo.repository.EdificioRepository;
import com.example.demo.repository.PostazioneRepository;
import com.example.demo.repository.PrenotazioneRepository;
import com.example.demo.repository.UtenteRepository;

@Service
public class PrenotazioneService {
    
    @Autowired
    private PrenotazioneRepository prenotazioneRepository;
    
    @Autowired
    private PostazioneRepository postazioneRepository;
    
    @Autowired
    private UtenteRepository utenteRepository;
    
    @Autowired
    private EdificioRepository edificioRepository;
    
    public Prenotazione prenotaPostazione(Long utenteId, Long postazioneId, LocalDate data) {
        // Verifica se l'utente ha già una prenotazione per quella data
        if (prenotazioneRepository.existsByUtenteIdAndData(utenteId, data)) {
            throw new IllegalStateException("L'utente ha già una prenotazione per questa data");
        }
        
        Postazione postazione = postazioneRepository.findById(postazioneId)
            .orElseThrow(() -> new IllegalArgumentException("Postazione non trovata"));
            
        // Verifica se l'edificio è completamente prenotato per quella data
        Long edificioId = postazione.getEdificio().getId();
        List<Prenotazione> prenotazioniEdificio = prenotazioneRepository
            .findByPostazioneEdificioIdAndData(edificioId, data);
            
        if (!prenotazioniEdificio.isEmpty()) {
            // Calcola il numero totale di posti occupati nell'edificio
            int postiOccupati = prenotazioniEdificio.stream()
                .mapToInt(p -> p.getPostazione().getNumeroMassimoOccupanti())
                .sum();
                
            // Calcola il numero totale di posti disponibili nell'edificio
            int postiTotali = postazioneRepository.findByEdificioId(edificioId).stream()
                .mapToInt(Postazione::getNumeroMassimoOccupanti)
                .sum();
                
            if (postiOccupati >= postiTotali) {
                throw new IllegalStateException(
                    "L'edificio ha raggiunto il numero massimo di prenotazioni per questa data");
            }
        }
        
        // Verifica se la postazione specifica è già prenotata
        if (prenotazioneRepository.existsByPostazioneIdAndData(postazioneId, data)) {
            throw new IllegalStateException("La postazione è già prenotata per questa data");
        }
        
        Utente utente = utenteRepository.findById(utenteId)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setUtente(utente);
        prenotazione.setPostazione(postazione);
        prenotazione.setData(data);
        
        return prenotazioneRepository.save(prenotazione);
    }
    
    public List<Postazione> cercaPostazioni(TipoPostazione tipo, String citta) {
        return postazioneRepository.findByTipoAndCitta(tipo, citta);
    }
    
    public List<Prenotazione> getPrenotazioniUtente(Long utenteId) {
        return prenotazioneRepository.findByUtenteId(utenteId);
    }
    
    public Prenotazione prenotaPostazioneByUsername(String username, String codicePostazione, LocalDate data) {
        Utente utente = utenteRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + username));
            
        Postazione postazione = postazioneRepository.findByCodice(codicePostazione)
            .orElseThrow(() -> new IllegalArgumentException("Postazione non trovata: " + codicePostazione));
            
        return prenotaPostazione(utente.getId(), postazione.getId(), data);
    }
    
    public List<Postazione> cercaPostazioniDisponibili(TipoPostazione tipo, String citta, LocalDate data) {
        List<Postazione> postazioni = postazioneRepository.findByTipoAndCitta(tipo, citta);
        return postazioni.stream()
            .filter(p -> !prenotazioneRepository.existsByPostazioneIdAndData(p.getId(), data))
            .collect(Collectors.toList());
    }
    
    public List<Prenotazione> getPrenotazioniByUsername(String username) {
        Utente utente = utenteRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + username));
        return prenotazioneRepository.findByUtenteId(utente.getId());
    }
    
    public List<Prenotazione> getAllPrenotazioni(String username) {
        if ("Administrator".equals(username)) {
            return prenotazioneRepository.findAll();
        } else {
            Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
            return prenotazioneRepository.findByUtenteId(utente.getId());
        }
    }
    
    public void deletePrenotazione(Long id, String username) {
        Prenotazione prenotazione = getPrenotazioneById(id);
        
        // Solo l'admin o il proprietario possono eliminare la prenotazione
        if (!"Administrator".equals(username) && 
            !prenotazione.getUtente().getUsername().equals(username)) {
            throw new IllegalStateException("Non hai i permessi per eliminare questa prenotazione");
        }
        
        prenotazioneRepository.deleteById(id);
    }
    
    public List<Postazione> getAllPostazioni() {
        return postazioneRepository.findAll();
    }
    
    public Postazione addPostazione(String codice, String descrizione, TipoPostazione tipo, 
                                  int maxOccupanti, Long edificioId) {
        Edificio edificio = edificioRepository.findById(edificioId)
            .orElseThrow(() -> new IllegalArgumentException("Edificio non trovato"));
            
        Postazione postazione = new Postazione();
        postazione.setCodice(codice);
        postazione.setDescrizione(descrizione);
        postazione.setTipo(tipo);
        postazione.setNumeroMassimoOccupanti(maxOccupanti);
        postazione.setEdificio(edificio);
        
        return postazioneRepository.save(postazione);
    }
    
    public void deletePostazione(String codice) {
        Postazione postazione = postazioneRepository.findByCodice(codice)
            .orElseThrow(() -> new IllegalArgumentException("Postazione non trovata"));
        postazioneRepository.delete(postazione);
    }

    public Prenotazione getPrenotazioneById(Long id) {
        return prenotazioneRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));
    }

    public Prenotazione modificaPrenotazione(Long id, LocalDate nuovaData, 
                                           String nuovoCodicePostazione, String username) {
        Prenotazione prenotazione = getPrenotazioneById(id);
        
        // Solo l'admin o il proprietario possono modificare la prenotazione
        if (!"Administrator".equals(username) && 
            !prenotazione.getUtente().getUsername().equals(username)) {
            throw new IllegalStateException("Non hai i permessi per modificare questa prenotazione");
        }
        
        if (nuovaData != null) {
            // Verifica disponibilità per la nuova data
            if (!prenotazione.getData().equals(nuovaData) && 
                prenotazioneRepository.existsByPostazioneIdAndData(
                    prenotazione.getPostazione().getId(), nuovaData)) {
                throw new IllegalStateException("Postazione già prenotata per la data selezionata");
            }
            prenotazione.setData(nuovaData);
        }
        
        if (nuovoCodicePostazione != null && !nuovoCodicePostazione.isEmpty()) {
            Postazione nuovaPostazione = postazioneRepository.findByCodice(nuovoCodicePostazione)
                .orElseThrow(() -> new IllegalArgumentException("Postazione non trovata"));
                
            // Verifica disponibilità della nuova postazione
            if (!prenotazione.getPostazione().getId().equals(nuovaPostazione.getId()) &&
                prenotazioneRepository.existsByPostazioneIdAndData(
                    nuovaPostazione.getId(), prenotazione.getData())) {
                throw new IllegalStateException("Nuova postazione già prenotata per questa data");
            }
            prenotazione.setPostazione(nuovaPostazione);
        }
        
        return prenotazioneRepository.save(prenotazione);
    }
} 