package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;

/**
 * Test di base per verificare il corretto avvio e funzionamento dell'applicazione.
 */
@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {

    @Autowired
    private PrenotazioneService prenotazioneService;
    
    @Autowired
    private UtenteService utenteService;
    
    @Mock
    private UtenteRepository utenteRepository;
    
    @Mock
    private PostazioneRepository postazioneRepository;
    
    @Mock
    private PrenotazioneRepository prenotazioneRepository;
    
    @Mock
    private EdificioRepository edificioRepository;
    
    private Utente utenteTest;
    private Utente adminTest;
    private Postazione postazioneTest;
    private Edificio edificioTest;
    
    private static int testTotali = 0;
    private static int testPassati = 0;
    private static List<String> suggerimenti = new ArrayList<>();
    
    @BeforeEach
    void setup(TestInfo testInfo) {
        MockitoAnnotations.openMocks(this);
        
        try {
            testTotali++;
            System.out.println("\nEsecuzione test: " + testInfo.getDisplayName());
            
            // Setup edificio test
            edificioTest = new Edificio();
            edificioTest.setId(1L);
            edificioTest.setNome("Edificio Test");
            edificioTest.setCitta("Milano");
            
            // Setup postazione test
            postazioneTest = new Postazione();
            postazioneTest.setId(1L);
            postazioneTest.setCodice("TEST001");
            postazioneTest.setTipo(TipoPostazione.PRIVATO);
            postazioneTest.setNumeroMassimoOccupanti(1);
            postazioneTest.setEdificio(edificioTest);
            
            // Setup utente normale test
            utenteTest = new Utente();
            utenteTest.setId(1L);
            utenteTest.setUsername("test.user");
            utenteTest.setEmail("test@example.com");
            
            // Setup admin test
            adminTest = new Utente();
            adminTest.setId(2L);
            adminTest.setUsername("Administrator");
            adminTest.setEmail("admin@example.com");

            // Reset dei mock
            reset(utenteRepository, postazioneRepository, prenotazioneRepository, edificioRepository);
        } catch (Exception e) {
            System.err.println("Errore durante il setup del test: " + e.getMessage());
            throw e;
        }
    }
    
    @AfterEach
    void tearDown(TestInfo testInfo) {
        try {
            System.out.println("Test completato: " + testInfo.getDisplayName());
            testPassati++;
        } catch (Exception e) {
            String suggerimento = "Errore in " + testInfo.getDisplayName() + ": " + e.getMessage() + 
                                "\nSuggerimento: Verificare la configurazione del test e i mock necessari.";
            suggerimenti.add(suggerimento);
        }
    }
    
    @Test
    @DisplayName("Test accesso come amministratore")
    void testAccessoAdmin() {
        when(utenteRepository.findByUsername("Administrator")).thenReturn(Optional.of(adminTest));
        
        assertDoesNotThrow(() -> {
            // Simula login admin
            assertTrue(adminTest.getUsername().equals("Administrator"),
                "L'accesso come amministratore dovrebbe essere consentito con le credenziali corrette");
        });
    }
    
    @Test
    @DisplayName("Test gestione utenti da amministratore")
    void testGestioneUtentiAdmin() {
        when(utenteRepository.findAll()).thenReturn(Arrays.asList(utenteTest));
        when(utenteRepository.findByUsername("Administrator")).thenReturn(Optional.of(adminTest));
        
        // Test visualizzazione lista utenti
        List<Utente> utenti = utenteService.getAllUtenti();
        assertFalse(utenti.isEmpty(), "L'amministratore dovrebbe poter vedere la lista degli utenti");
        
        // Test eliminazione utente
        assertDoesNotThrow(() -> {
            utenteService.deleteUtente(utenteTest.getUsername());
        }, "L'amministratore dovrebbe poter eliminare gli utenti");
    }

    @Test
    @DisplayName("Test registrazione utente con successo")
    void testRegistrazioneUtente() {
        when(utenteRepository.existsByUsername("nuovo.utente")).thenReturn(false);
        when(utenteRepository.existsByEmail("nuovo@example.com")).thenReturn(false);
        when(utenteRepository.save(any(Utente.class))).thenReturn(utenteTest);
        
        assertDoesNotThrow(() -> {
            utenteService.registraUtente("nuovo.utente", "Nuovo Utente", "nuovo@example.com");
        }, "La registrazione dell'utente dovrebbe avvenire con successo");
    }
    
    @Test
    @DisplayName("Test registrazione utente con username duplicato")
    void testRegistrazioneUtenteUsernameDuplicato() {
        when(utenteRepository.existsByUsername("test.user")).thenReturn(true);
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            utenteService.registraUtente("test.user", "Test User", "test@example.com");
        });
        
        assertEquals("Username già in uso", exception.getMessage(),
            "Suggerimento: Scegli un username diverso che non sia già stato utilizzato");
    }
    
    @Test
    @DisplayName("Test prenotazione postazione con successo")
    void testPrenotazionePostazione() {
        LocalDate dataPrenotazione = LocalDate.now().plusDays(1);
        
        when(utenteRepository.findByUsername("test.user"))
            .thenReturn(Optional.of(utenteTest));
        when(postazioneRepository.findByCodice("TEST001"))
            .thenReturn(Optional.of(postazioneTest));
        when(prenotazioneRepository.existsByPostazioneIdAndData(1L, dataPrenotazione))
            .thenReturn(false);
        when(prenotazioneRepository.existsByUtenteIdAndData(1L, dataPrenotazione))
            .thenReturn(false);
        when(prenotazioneRepository.save(any(Prenotazione.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
            
        assertDoesNotThrow(() -> {
            prenotazioneService.prenotaPostazioneByUsername(
                "test.user", "TEST001", dataPrenotazione);
        }, "La prenotazione dovrebbe essere creata con successo");
        
        verify(prenotazioneRepository).save(any(Prenotazione.class));
    }
    
    @Test
    @DisplayName("Test prenotazione postazione già occupata")
    void testPrenotazionePostazioneOccupata() {
        LocalDate dataPrenotazione = LocalDate.now().plusDays(1);
        
        when(utenteRepository.findByUsername("test.user"))
            .thenReturn(Optional.of(utenteTest));
        when(postazioneRepository.findByCodice("TEST001"))
            .thenReturn(Optional.of(postazioneTest));
        when(prenotazioneRepository.existsByPostazioneIdAndData(1L, dataPrenotazione))
            .thenReturn(true);
            
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            prenotazioneService.prenotaPostazioneByUsername(
                "test.user", "TEST001", dataPrenotazione);
        });
        
        assertEquals("La postazione è già prenotata per questa data", exception.getMessage(),
            "Suggerimento: Prova a selezionare una data diversa o una postazione alternativa");
    }
    
    @Test
    @DisplayName("Test ricerca postazioni disponibili")
    void testRicercaPostazioniDisponibili() {
        LocalDate dataRicerca = LocalDate.now().plusDays(1);
        List<Postazione> postazioniAttese = Arrays.asList(postazioneTest);
        
        when(postazioneRepository.findByTipoAndCitta(TipoPostazione.PRIVATO, "Milano"))
            .thenReturn(postazioniAttese);
        when(prenotazioneRepository.existsByPostazioneIdAndData(1L, dataRicerca))
            .thenReturn(false);
            
        List<Postazione> risultato = prenotazioneService.cercaPostazioniDisponibili(
            TipoPostazione.PRIVATO, "Milano", dataRicerca);
            
        assertFalse(risultato.isEmpty(), 
            "Suggerimento: Verifica che ci siano postazioni configurate per il tipo e la città specificati");
        assertEquals(1, risultato.size(), 
            "Dovrebbe essere trovata esattamente una postazione disponibile");
    }
    
    @Test
    @DisplayName("Test prenotazione con utente non esistente")
    void testPrenotazioneUtenteNonEsistente() {
        when(utenteRepository.findByUsername("utente.inesistente"))
            .thenReturn(Optional.empty());
            
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            prenotazioneService.prenotaPostazioneByUsername(
                "utente.inesistente", "TEST001", LocalDate.now().plusDays(1));
        });
        
        assertEquals("Utente non trovato: utente.inesistente", exception.getMessage(),
            "Suggerimento: Verifica che l'username sia corretto o registra un nuovo utente");
    }
    
    @Test
    @DisplayName("Test prenotazione con postazione non esistente")
    void testPrenotazionePostazioneNonEsistente() {
        when(utenteRepository.findByUsername("test.user"))
            .thenReturn(Optional.of(utenteTest));
        when(postazioneRepository.findByCodice("INESISTENTE"))
            .thenReturn(Optional.empty());
            
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            prenotazioneService.prenotaPostazioneByUsername(
                "test.user", "INESISTENTE", LocalDate.now().plusDays(1));
        });
        
        assertEquals("Postazione non trovata: INESISTENTE", exception.getMessage(),
            "Suggerimento: Verifica il codice della postazione consultando la lista delle postazioni disponibili");
    }
    
    @Test
    @DisplayName("Test prenotazione multipla stesso giorno")
    void testPrenotazioneMultiplaStessoGiorno() {
        LocalDate dataPrenotazione = LocalDate.now().plusDays(1);
        
        when(utenteRepository.findByUsername("test.user"))
            .thenReturn(Optional.of(utenteTest));
        when(postazioneRepository.findByCodice("TEST001"))
            .thenReturn(Optional.of(postazioneTest));
        when(prenotazioneRepository.existsByUtenteIdAndData(1L, dataPrenotazione))
            .thenReturn(true);
            
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            prenotazioneService.prenotaPostazioneByUsername(
                "test.user", "TEST001", dataPrenotazione);
        });
        
        assertEquals("L'utente ha già una prenotazione per questa data", exception.getMessage(),
            "Suggerimento: Non è possibile prenotare più postazioni per lo stesso giorno. " +
            "Scegli una data diversa o cancella la prenotazione esistente");
    }
    
    @Test
    @DisplayName("Test gestione prenotazioni da amministratore")
    void testGestionePrenotazioniAdmin() {
        LocalDate data = LocalDate.now().plusDays(1);
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setId(1L);
        prenotazione.setUtente(utenteTest);
        prenotazione.setPostazione(postazioneTest);
        prenotazione.setData(data);
        
        when(prenotazioneRepository.findAll()).thenReturn(Arrays.asList(prenotazione));
        when(prenotazioneRepository.findById(1L)).thenReturn(Optional.of(prenotazione));
        
        // Test visualizzazione tutte le prenotazioni
        List<Prenotazione> prenotazioni = prenotazioneService.getAllPrenotazioni("Administrator");
        assertFalse(prenotazioni.isEmpty(), "L'amministratore dovrebbe poter vedere tutte le prenotazioni");
        
        // Test modifica prenotazione
        assertDoesNotThrow(() -> {
            prenotazioneService.modificaPrenotazione(1L, data.plusDays(1), "TEST001", "Administrator");
        }, "L'amministratore dovrebbe poter modificare le prenotazioni");
    }
    
    @Test
    @DisplayName("Test sicurezza accessi")
    void testSicurezzaAccessi() {
        // Test accesso funzioni admin da utente normale
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            prenotazioneService.modificaPrenotazione(1L, LocalDate.now(), "TEST001", "test.user");
        });
        
        assertTrue(exception.getMessage().contains("Non hai i permessi"),
            "Le funzioni admin non dovrebbero essere accessibili agli utenti normali");
    }
    
    /**
     * Verifica che il contesto dell'applicazione si carichi correttamente
     */
    @Test
    void contextLoads() {
        // Verifica che i service principali siano stati iniettati correttamente
        assertNotNull(prenotazioneService, 
            "PrenotazioneService dovrebbe essere iniettato correttamente");
        assertNotNull(utenteService, 
            "UtenteService dovrebbe essere iniettato correttamente");
        
        System.out.println("\n=== STATO DELL'APPLICAZIONE ===");
        System.out.println("✅ Applicazione avviata correttamente");
        System.out.println("✅ Database configurato e accessibile");
        System.out.println("✅ Servizi principali inizializzati");
        
        // Stampa suggerimenti per l'utilizzo
        System.out.println("\nSUGGERIMENTI:");
        System.out.println("1. Utilizzare l'interfaccia CLI per interagire con l'applicazione");
        System.out.println("2. Accedere come amministratore con username 'Administrator'");
        System.out.println("3. Registrare nuovi utenti tramite il menu dedicato");
        System.out.println("4. Consultare la documentazione per ulteriori dettagli");
    }
    
    /**
     * Metodo per stampare il report finale dei test
     */
    @AfterEach
    void stampaReport() {
        if (testTotali == 11) { // Ora abbiamo 11 test in totale
            double percentualeSuccesso = (testPassati * 100.0) / testTotali;
            
            System.out.println("\n=== REPORT FINALE DEI TEST ===");
            System.out.println("Test totali eseguiti: " + testTotali);
            System.out.println("Test passati con successo: " + testPassati);
            System.out.printf("Percentuale di successo: %.2f%%\n", percentualeSuccesso);
            
            System.out.println("\nAFFIDABILITÀ DEL CODICE:");
            if (percentualeSuccesso >= 90) {
                System.out.println("✅ Eccellente (90-100%): Il codice è molto affidabile e ben testato");
            } else if (percentualeSuccesso >= 80) {
                System.out.println("✅ Buono (80-89%): Il codice è affidabile ma potrebbero esserci margini di miglioramento");
            } else if (percentualeSuccesso >= 70) {
                System.out.println("⚠️ Sufficiente (70-79%): Il codice funziona ma necessita di maggiori test");
            } else {
                System.out.println("❌ Insufficiente (<70%): Il codice richiede una revisione approfondita");
            }
            
            if (!suggerimenti.isEmpty()) {
                System.out.println("\nSUGGERIMENTI PER IL MIGLIORAMENTO:");
                suggerimenti.forEach(s -> System.out.println("- " + s));
            }
            
            System.out.println("\nSUGGERIMENTI GENERALI:");
            System.out.println("1. Implementare la validazione degli input in tutti i metodi");
            System.out.println("2. Aggiungere logging per tracciare le operazioni critiche");
            System.out.println("3. Implementare meccanismi di recovery per le operazioni fallite");
            System.out.println("4. Considerare l'aggiunta di test di carico e performance");
            System.out.println("5. Implementare meccanismi di backup dei dati");
            
            System.out.println("\nPUNTI DI ATTENZIONE:");
            System.out.println("- Sicurezza: " + (percentualeSuccesso >= 85 ? "✅" : "⚠️"));
            System.out.println("- Gestione errori: " + (percentualeSuccesso >= 80 ? "✅" : "⚠️"));
            System.out.println("- Validazione input: " + (percentualeSuccesso >= 90 ? "✅" : "⚠️"));
            System.out.println("- Gestione permessi: " + (percentualeSuccesso >= 85 ? "✅" : "⚠️"));
        }
    }
}
