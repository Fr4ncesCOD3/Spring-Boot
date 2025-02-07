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

/**
 * Service che gestisce la logica di business per le prenotazioni delle postazioni.
 * Fornisce metodi per la gestione delle prenotazioni, ricerca postazioni e verifica disponibilità.
 */
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
    
    /**
     * Crea una nuova prenotazione per una postazione.
     * Verifica la disponibilità della postazione e i vincoli di prenotazione.
     * 
     * @param utenteId ID dell'utente che effettua la prenotazione
     * @param postazioneId ID della postazione da prenotare
     * @param data Data per cui si vuole prenotare
     * @return La prenotazione creata
     * @throws IllegalStateException se ci sono conflitti di prenotazione
     * @throws IllegalArgumentException se utente o postazione non esistono
     */
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
    
    /**
     * Cerca postazioni per tipo e città.
     * 
     * @param tipo Tipo di postazione desiderato
     * @param citta Città in cui cercare
     * @return Lista delle postazioni che corrispondono ai criteri
     */
    public List<Postazione> cercaPostazioni(TipoPostazione tipo, String citta) {
        return postazioneRepository.findByTipoAndCitta(tipo, citta);
    }
    
    /**
     * Recupera tutte le prenotazioni di un utente.
     * 
     * @param utenteId ID dell'utente
     * @return Lista delle prenotazioni dell'utente
     */
    public List<Prenotazione> getPrenotazioniUtente(Long utenteId) {
        return prenotazioneRepository.findByUtenteId(utenteId);
    }
    
    /**
     * Prenota una postazione usando username e codice postazione.
     * 
     * @param username Username dell'utente
     * @param codicePostazione Codice della postazione
     * @param data Data della prenotazione
     * @return La prenotazione creata
     */
    public Prenotazione prenotaPostazioneByUsername(String username, String codicePostazione, LocalDate data) {
        Utente utente = utenteRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + username));
            
        Postazione postazione = postazioneRepository.findByCodice(codicePostazione)
            .orElseThrow(() -> new IllegalArgumentException("Postazione non trovata: " + codicePostazione));
            
        return prenotaPostazione(utente.getId(), postazione.getId(), data);
    }
    
    /**
     * Cerca postazioni disponibili per tipo e città in una data specifica.
     * 
     * @param tipo Tipo di postazione
     * @param citta Città
     * @param data Data per cui verificare la disponibilità
     * @return Lista delle postazioni disponibili
     */
    public List<Postazione> cercaPostazioniDisponibili(TipoPostazione tipo, String citta, LocalDate data) {
        List<Postazione> postazioni = postazioneRepository.findByTipoAndCitta(tipo, citta);
        return postazioni.stream()
            .filter(p -> !prenotazioneRepository.existsByPostazioneIdAndData(p.getId(), data))
            .collect(Collectors.toList());
    }
    
    /**
     * Recupera le prenotazioni di un utente tramite username.
     * 
     * @param username Username dell'utente
     * @return Lista delle prenotazioni dell'utente
     */
    public List<Prenotazione> getPrenotazioniByUsername(String username) {
        Utente utente = utenteRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + username));
        return prenotazioneRepository.findByUtenteId(utente.getId());
    }
    
    /**
     * Recupera tutte le prenotazioni (solo per admin) o solo quelle dell'utente.
     * 
     * @param username Username dell'utente o "Administrator"
     * @return Lista delle prenotazioni
     */
    public List<Prenotazione> getAllPrenotazioni(String username) {
        if ("Administrator".equals(username)) {
            return prenotazioneRepository.findAll();
        } else {
            Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
            return prenotazioneRepository.findByUtenteId(utente.getId());
        }
    }
    
    /**
     * Elimina una prenotazione se l'utente ha i permessi necessari.
     * 
     * @param id ID della prenotazione
     * @param username Username dell'utente che richiede l'eliminazione
     */
    public void deletePrenotazione(Long id, String username) {
        Prenotazione prenotazione = getPrenotazioneById(id);
        
        // Solo l'admin o il proprietario possono eliminare la prenotazione
        if (!"Administrator".equals(username) && 
            !prenotazione.getUtente().getUsername().equals(username)) {
            throw new IllegalStateException("Non hai i permessi per eliminare questa prenotazione");
        }
        
        prenotazioneRepository.deleteById(id);
    }
    
    /**
     * Recupera tutte le postazioni disponibili.
     * 
     * @return Lista di tutte le postazioni
     */
    public List<Postazione> getAllPostazioni() {
        return postazioneRepository.findAll();
    }
    
    /**
     * Aggiunge una nuova postazione.
     * 
     * @param codice Codice univoco della postazione
     * @param descrizione Descrizione della postazione
     * @param tipo Tipo di postazione
     * @param maxOccupanti Numero massimo di occupanti
     * @param edificioId ID dell'edificio
     * @return La postazione creata
     */
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
    
    /**
     * Elimina una postazione dato il suo codice.
     * 
     * @param codice Codice della postazione da eliminare
     */
    public void deletePostazione(String codice) {
        Postazione postazione = postazioneRepository.findByCodice(codice)
            .orElseThrow(() -> new IllegalArgumentException("Postazione non trovata"));
        postazioneRepository.delete(postazione);
    }

    /**
     * Recupera una prenotazione dato il suo ID.
     * 
     * @param id ID della prenotazione
     * @return La prenotazione trovata
     */
    public Prenotazione getPrenotazioneById(Long id) {
        return prenotazioneRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));
    }

    /**
     * Modifica una prenotazione esistente.
     * 
     * @param id ID della prenotazione da modificare
     * @param nuovaData Nuova data (opzionale)
     * @param nuovoCodicePostazione Nuovo codice postazione (opzionale)
     * @param username Username dell'utente che richiede la modifica
     * @return La prenotazione modificata
     */
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