package com.example.demo.config;

// Import delle dipendenze Spring necessarie
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demo.model.Edificio;
import com.example.demo.model.Postazione;
import com.example.demo.model.TipoPostazione;
import com.example.demo.model.Utente;
import com.example.demo.repository.EdificioRepository;
import com.example.demo.repository.PostazioneRepository;
import com.example.demo.repository.UtenteRepository;

/**
 * Classe responsabile del caricamento dei dati iniziali nel database.
 * Viene eseguita all'avvio dell'applicazione grazie all'implementazione di CommandLineRunner.
 * L'annotazione @Order(1) garantisce che venga eseguita per prima.
 */
@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    // Injection dei repository necessari per l'accesso al database
    @Autowired
    private EdificioRepository edificioRepository;
    
    @Autowired
    private PostazioneRepository postazioneRepository;
    
    @Autowired
    private UtenteRepository utenteRepository;

    /**
     * Metodo eseguito all'avvio dell'applicazione.
     * Verifica se il database è vuoto e in tal caso lo popola con i dati iniziali.
     */
    @Override
    public void run(String... args) {
        try {
            // Verifica se il database è già inizializzato
            if (edificioRepository.count() == 0) {
                initializeData();
                System.out.println("Database inizializzato con successo.");
            }
        } catch (Exception e) {
            System.err.println("Errore di inizializzazione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Metodo che inizializza il database con i dati di esempio.
     * Crea edifici, postazioni e utenti di test.
     */
    private void initializeData() {
        // Creazione degli edifici nelle principali città italiane
        Edificio edificioMilano1 = createEdificio("Grattacielo Milano", "Via Pirelli 1", "Milano");
        Edificio edificioMilano2 = createEdificio("Milano Centro", "Piazza Duomo 1", "Milano");
        Edificio edificioRoma1 = createEdificio("Roma EUR", "Viale Europa 190", "Roma");
        Edificio edificioRoma2 = createEdificio("Roma Centro", "Via del Corso 12", "Roma");
        Edificio edificioNapoli = createEdificio("Napoli Business", "Via Toledo 256", "Napoli");
        Edificio edificioTorino = createEdificio("Torino Innovation", "Corso Vittorio 45", "Torino");
        
        // Creazione delle postazioni per ogni edificio
        // Milano - Grattacielo
        createPostazione("MI001", "Executive Office", TipoPostazione.PRIVATO, 1, edificioMilano1);
        createPostazione("MI002", "Open Space Piano 10", TipoPostazione.OPENSPACE, 15, edificioMilano1);
        createPostazione("MI003", "Sala Meeting A", TipoPostazione.SALA_RIUNIONI, 20, edificioMilano1);
        
        // Milano - Centro
        createPostazione("MI004", "Ufficio Design", TipoPostazione.PRIVATO, 2, edificioMilano2);
        createPostazione("MI005", "Co-Working Space", TipoPostazione.OPENSPACE, 25, edificioMilano2);
        
        // Roma EUR
        createPostazione("RM001", "Ufficio Direzionale", TipoPostazione.PRIVATO, 1, edificioRoma1);
        createPostazione("RM002", "Sala Conferenze", TipoPostazione.SALA_RIUNIONI, 50, edificioRoma1);
        createPostazione("RM003", "Open Space Creativo", TipoPostazione.OPENSPACE, 30, edificioRoma1);
        
        // Roma Centro
        createPostazione("RM004", "Studio Privato", TipoPostazione.PRIVATO, 2, edificioRoma2);
        createPostazione("RM005", "Sala Meeting B", TipoPostazione.SALA_RIUNIONI, 15, edificioRoma2);
        
        // Napoli
        createPostazione("NA001", "Ufficio Vista Mare", TipoPostazione.PRIVATO, 2, edificioNapoli);
        createPostazione("NA002", "Open Space Moderno", TipoPostazione.OPENSPACE, 20, edificioNapoli);
        createPostazione("NA003", "Sala Meeting Mare", TipoPostazione.SALA_RIUNIONI, 25, edificioNapoli);
        
        // Torino
        createPostazione("TO001", "Smart Office", TipoPostazione.PRIVATO, 1, edificioTorino);
        createPostazione("TO002", "Innovation Lab", TipoPostazione.OPENSPACE, 40, edificioTorino);
        createPostazione("TO003", "Meeting Room Future", TipoPostazione.SALA_RIUNIONI, 30, edificioTorino);

        // Creazione degli utenti di esempio
        createUtente("mario.rossi", "Mario Rossi", "mario.rossi@example.com");
        createUtente("laura.bianchi", "Laura Bianchi", "laura.bianchi@example.com");
        createUtente("giuseppe.verdi", "Giuseppe Verdi", "giuseppe.verdi@example.com");
        createUtente("anna.neri", "Anna Neri", "anna.neri@example.com");
        createUtente("marco.gialli", "Marco Gialli", "marco.gialli@example.com");
        createUtente("sofia.russo", "Sofia Russo", "sofia.russo@example.com");
        createUtente("luca.ferrari", "Luca Ferrari", "luca.ferrari@example.com");
        createUtente("giulia.marino", "Giulia Marino", "giulia.marino@example.com");
        createUtente("andrea.costa", "Andrea Costa", "andrea.costa@example.com");
        createUtente("valentina.greco", "Valentina Greco", "valentina.greco@example.com");
    }

    /**
     * Crea e salva un nuovo edificio nel database.
     * @param nome Nome dell'edificio
     * @param indirizzo Indirizzo dell'edificio
     * @param citta Città dove si trova l'edificio
     * @return L'edificio creato e salvato
     */
    private Edificio createEdificio(String nome, String indirizzo, String citta) {
        Edificio edificio = new Edificio();
        edificio.setNome(nome);
        edificio.setIndirizzo(indirizzo);
        edificio.setCitta(citta);
        return edificioRepository.save(edificio);
    }

    /**
     * Crea e salva una nuova postazione nel database.
     * @param codice Codice identificativo della postazione
     * @param descrizione Descrizione della postazione
     * @param tipo Tipo della postazione (PRIVATO, OPENSPACE, SALA_RIUNIONI)
     * @param maxOccupanti Numero massimo di occupanti
     * @param edificio Edificio a cui appartiene la postazione
     */
    private void createPostazione(String codice, String descrizione, TipoPostazione tipo, 
                                int maxOccupanti, Edificio edificio) {
        Postazione postazione = new Postazione();
        postazione.setCodice(codice);
        postazione.setDescrizione(descrizione);
        postazione.setTipo(tipo);
        postazione.setNumeroMassimoOccupanti(maxOccupanti);
        postazione.setEdificio(edificio);
        postazioneRepository.save(postazione);
    }

    /**
     * Crea e salva un nuovo utente nel database se non esiste già un utente con la stessa email.
     * @param username Username dell'utente
     * @param nomeCompleto Nome completo dell'utente
     * @param email Email dell'utente
     */
    private void createUtente(String username, String nomeCompleto, String email) {
        if (!utenteRepository.existsByEmail(email)) {
            Utente utente = new Utente();
            utente.setUsername(username);
            utente.setNomeCompleto(nomeCompleto);
            utente.setEmail(email);
            utenteRepository.save(utente);
        }
    }
} 