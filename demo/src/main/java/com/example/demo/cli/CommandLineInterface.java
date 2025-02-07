package com.example.demo.cli;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demo.model.Postazione;
import com.example.demo.model.Prenotazione;
import com.example.demo.model.TipoPostazione;
import com.example.demo.model.Utente;
import com.example.demo.service.PrenotazioneService;
import com.example.demo.service.UtenteService;

@Component
@Order(2)
public class CommandLineInterface implements CommandLineRunner {

    private static final String LINE_SEPARATOR = "\n" + "=".repeat(50) + "\n";
    private static final String DATE_FORMAT = "YYYY-MM-DD";
    
    @Autowired
    private PrenotazioneService prenotazioneService;
    @Autowired
    private UtenteService utenteService;
    
    private Scanner scanner = new Scanner(System.in);
    private String currentUser = null;
    private boolean isAdmin = false;

    @Override
    public void run(String... args) {
        while (true) {
            clearScreen();
            printWelcome();
            
            if (currentUser == null) {
                if (!autenticazione()) {
                    System.out.println("\nGrazie per aver utilizzato il nostro servizio!");
                    break;
                }
            } else {
                try {
                    printMenu();
                    int scelta = getValidChoice(0, isAdmin ? 7 : 4);

                    clearScreen();
                    switch (scelta) {
                        case 1 -> cercaPostazioni();
                        case 2 -> effettuaPrenotazione();
                        case 3 -> visualizzaPrenotazioni();
                        case 4 -> mostraInformazioniRiferimento();
                        case 5 -> {
                            if (isAdmin) gestisciUtenti();
                        }
                        case 6 -> {
                            if (isAdmin) gestisciPrenotazioni();
                        }
                        case 7 -> {
                            if (isAdmin) gestisciPostazioni();
                        }
                        case 0 -> {
                            logout();
                            continue;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("\nErrore inaspettato. Riprova.");
                }
                
                System.out.println("\nPremi INVIO per continuare...");
                scanner.nextLine();
                clearScreen();
            }
        }
    }

    private boolean autenticazione() {
        System.out.println("\nSeleziona il tipo di accesso:");
        System.out.println("1. Amministratore");
        System.out.println("2. Utente");
        System.out.println("0. Esci");
        
        int scelta = getValidChoice(0, 2);
        
        if (scelta == 0) {
            return false;
        }
        
        if (scelta == 1) {
            return autenticazioneAdmin();
        } else {
            return autenticazioneUtente();
        }
    }

    private boolean autenticazioneAdmin() {
        System.out.println("\n=== Accesso Amministratore ===");
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if ("admin123".equals(password)) { // In un'app reale, usare password criptate
            isAdmin = true;
            currentUser = "Administrator";
            System.out.println("Benvenuto Administrator!");
            return true;
        } else {
            System.out.println("Password non valida!");
            return false;
        }
    }

    private boolean autenticazioneUtente() {
        System.out.println("\n=== Accesso Utente ===");
        System.out.print("Username (nome.cognome): ");
        String username = scanner.nextLine().toLowerCase();
        
        try {
            if (utenteService.existsByUsername(username)) {
                currentUser = username;
                System.out.println("Bentornato " + username + "!");
                return true;
            } else {
                System.out.println("\nUtente non trovato. Vuoi registrarti? (s/n)");
                if (scanner.nextLine().trim().toLowerCase().startsWith("s")) {
                    registrazioneUtente(username);
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            System.out.println("Errore durante l'autenticazione: " + e.getMessage());
            return false;
        }
    }

    private void registrazioneUtente(String username) {
        System.out.println("\n=== Registrazione Nuovo Utente ===");
        System.out.print("Nome completo: ");
        String nomeCompleto = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        try {
            utenteService.registraUtente(username, nomeCompleto, email);
            currentUser = username;
            System.out.println("Registrazione completata! Benvenuto " + username + "!");
        } catch (Exception e) {
            System.out.println("Errore durante la registrazione: " + e.getMessage());
        }
    }

    private void printMenu() {
        System.out.println("\nSeleziona un'operazione:");
        System.out.println("1. Cerca postazioni disponibili");
        System.out.println("2. Effettua una prenotazione");
        System.out.println("3. Visualizza le tue prenotazioni");
        System.out.println("4. Visualizza informazioni di riferimento");
        if (isAdmin) {
            System.out.println("5. Gestione utenti");
            System.out.println("6. Gestione prenotazioni");
            System.out.println("7. Gestione postazioni");
        }
        System.out.println("0. Esci");
        System.out.print("\nScelta: ");
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printWelcome() {
        System.out.println(LINE_SEPARATOR);
        System.out.println("   Benvenuto nel Sistema di Gestione Prenotazioni   ");
        System.out.println(LINE_SEPARATOR);
    }

    private int getValidChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.printf("Inserisci un numero tra %d e %d: ", min, max);
        }
    }

    private LocalDate getValidDate() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                LocalDate date = LocalDate.parse(input);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("La data non può essere nel passato. Riprova (" + DATE_FORMAT + "): ");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Formato data non valido. Riprova (" + DATE_FORMAT + "): ");
            }
        }
    }

    private void cercaPostazioni() {
        System.out.println("\n=== Ricerca Postazioni ===");
        
        System.out.println("Tipo postazione (PRIVATO, OPENSPACE, SALA_RIUNIONI): ");
        TipoPostazione tipo = getTipoPostazione();
        
        System.out.println("Città: ");
        String citta = scanner.nextLine().trim();
        
        System.out.println("Data (" + DATE_FORMAT + "): ");
        LocalDate data = getValidDate();

        try {
            List<Postazione> postazioni = prenotazioneService.cercaPostazioniDisponibili(tipo, citta, data);
            
            if (postazioni.isEmpty()) {
                System.out.println("\nNessuna postazione disponibile per i criteri selezionati.");
                return;
            }

            System.out.println("\nPostazioni disponibili:");
            postazioni.forEach(p -> System.out.printf(
                "- Codice: %s | %s | Max occupanti: %d | %s%n",
                p.getCodice(), 
                p.getDescrizione(), 
                p.getNumeroMassimoOccupanti(), 
                p.getEdificio().getNome()
            ));
        } catch (Exception e) {
            System.out.println("Errore nella ricerca. Riprova.");
        }
    }

    private TipoPostazione getTipoPostazione() {
        while (true) {
            try {
                return TipoPostazione.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo non valido. Inserisci PRIVATO, OPENSPACE o SALA_RIUNIONI: ");
            }
        }
    }

    private void effettuaPrenotazione() {
        try {
            System.out.println("\n=== Nuova Prenotazione ===");
            System.out.println("Username: ");
            String username = scanner.nextLine();
            
            System.out.println("Codice postazione: ");
            String codicePostazione = scanner.nextLine();
            
            System.out.println("Data (YYYY-MM-DD): ");
            String dataString = scanner.nextLine();
            LocalDate data = LocalDate.parse(dataString);

            Prenotazione prenotazione = prenotazioneService.prenotaPostazioneByUsername(
                username, codicePostazione, data);
            System.out.println("Prenotazione effettuata con successo!");
        } catch (DateTimeParseException e) {
            System.out.println("Errore: Formato data non valido. Usa il formato YYYY-MM-DD");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void visualizzaPrenotazioni() {
        System.out.println("\n=== Le tue Prenotazioni ===");
        System.out.println("Username: ");
        String username = scanner.nextLine();

        try {
            List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniByUsername(username);
            
            if (prenotazioni.isEmpty()) {
                System.out.println("Non hai prenotazioni attive.");
                return;
            }

            prenotazioni.forEach(p -> System.out.printf(
                "Data: %s, Postazione: %s, Edificio: %s%n",
                p.getData(), p.getPostazione().getCodice(), 
                p.getPostazione().getEdificio().getNome()
            ));
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void mostraInformazioniRiferimento() {
        System.out.println("\n=== INFORMAZIONI DI RIFERIMENTO ===");
        
        System.out.println("\nUTENTI DISPONIBILI:");
        System.out.println("---------------------");
        System.out.println("Username            | Nome Completo");
        System.out.println("---------------------");
        System.out.println("mario.rossi         | Mario Rossi");
        System.out.println("laura.bianchi       | Laura Bianchi");
        System.out.println("giuseppe.verdi      | Giuseppe Verdi");
        System.out.println("anna.neri           | Anna Neri");
        System.out.println("marco.gialli        | Marco Gialli");
        System.out.println("sofia.russo         | Sofia Russo");
        System.out.println("luca.ferrari        | Luca Ferrari");
        System.out.println("giulia.marino       | Giulia Marino");
        System.out.println("andrea.costa        | Andrea Costa");
        System.out.println("valentina.greco     | Valentina Greco");

        System.out.println("\nPOSTAZIONI PER CITTÀ:");
        System.out.println("---------------------");
        
        System.out.println("\nMILANO:");
        System.out.println("MI001 - Executive Office (Privato)");
        System.out.println("MI002 - Open Space Piano 10 (Open Space)");
        System.out.println("MI003 - Sala Meeting A (Sala Riunioni)");
        System.out.println("MI004 - Ufficio Design (Privato)");
        System.out.println("MI005 - Co-Working Space (Open Space)");
        
        System.out.println("\nROMA:");
        System.out.println("RM001 - Ufficio Direzionale (Privato)");
        System.out.println("RM002 - Sala Conferenze (Sala Riunioni)");
        System.out.println("RM003 - Open Space Creativo (Open Space)");
        System.out.println("RM004 - Studio Privato (Privato)");
        System.out.println("RM005 - Sala Meeting B (Sala Riunioni)");
        
        System.out.println("\nNAPOLI:");
        System.out.println("NA001 - Ufficio Vista Mare (Privato)");
        System.out.println("NA002 - Open Space Moderno (Open Space)");
        System.out.println("NA003 - Sala Meeting Mare (Sala Riunioni)");
        
        System.out.println("\nTORINO:");
        System.out.println("TO001 - Smart Office (Privato)");
        System.out.println("TO002 - Innovation Lab (Open Space)");
        System.out.println("TO003 - Meeting Room Future (Sala Riunioni)");

        System.out.println("\nTIPOLOGIE DISPONIBILI:");
        System.out.println("---------------------");
        System.out.println("- PRIVATO");
        System.out.println("- OPENSPACE");
        System.out.println("- SALA_RIUNIONI");
    }

    // Metodi per l'amministratore
    private void gestisciUtenti() {
        System.out.println("\n=== Gestione Utenti ===");
        System.out.println("1. Lista utenti");
        System.out.println("2. Elimina utente");
        System.out.println("0. Indietro");
        
        int scelta = getValidChoice(0, 2);
        switch (scelta) {
            case 1 -> mostraListaUtenti();
            case 2 -> eliminaUtente();
        }
    }

    private void gestisciPrenotazioni() {
        while (true) {
            System.out.println("\n=== Gestione Prenotazioni ===");
            System.out.println("1. Lista tutte le prenotazioni");
            System.out.println("2. Modifica prenotazione");
            System.out.println("3. Elimina prenotazione");
            System.out.println("0. Indietro");
            
            int scelta = getValidChoice(0, 3);
            switch (scelta) {
                case 1 -> mostraTuttePrenotazioni();
                case 2 -> modificaPrenotazione();
                case 3 -> eliminaPrenotazione();
                case 0 -> {
                    return;
                }
            }
            
            System.out.println("\nPremi INVIO per continuare...");
            scanner.nextLine();
        }
    }

    private void gestisciPostazioni() {
        System.out.println("\n=== Gestione Postazioni ===");
        System.out.println("1. Lista postazioni");
        System.out.println("2. Aggiungi postazione");
        System.out.println("3. Elimina postazione");
        System.out.println("0. Indietro");
        
        int scelta = getValidChoice(0, 3);
        switch (scelta) {
            case 1 -> mostraListaPostazioni();
            case 2 -> aggiungiPostazione();
            case 3 -> eliminaPostazione();
        }
    }

    // Implementazione dei metodi di gestione admin
    private void mostraListaUtenti() {
        System.out.println("\n=== Lista Utenti ===");
        List<Utente> utenti = utenteService.getAllUtenti();
        if (utenti.isEmpty()) {
            System.out.println("Nessun utente registrato.");
            return;
        }
        
        System.out.println("\nUsername            | Nome Completo           | Email");
        System.out.println("-".repeat(60));
        utenti.forEach(u -> System.out.printf("%-18s | %-21s | %s%n",
            u.getUsername(),
            u.getNomeCompleto(),
            u.getEmail()
        ));
    }

    private void eliminaUtente() {
        System.out.println("\n=== Elimina Utente ===");
        System.out.print("Inserisci username da eliminare: ");
        String username = scanner.nextLine().trim();
        
        try {
            utenteService.deleteUtente(username);
            System.out.println("Utente eliminato con successo.");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void mostraTuttePrenotazioni() {
        System.out.println("\n=== Lista Prenotazioni ===");
        List<Prenotazione> prenotazioni = prenotazioneService.getAllPrenotazioni(currentUser);
        if (prenotazioni.isEmpty()) {
            System.out.println("Nessuna prenotazione presente.");
            return;
        }

        System.out.println("\nID    | Data       | Utente            | Postazione | Edificio");
        System.out.println("-".repeat(70));
        prenotazioni.forEach(p -> System.out.printf("%-5d | %s | %-17s | %-10s | %s%n",
            p.getId(),
            p.getData(),
            p.getUtente().getUsername(),
            p.getPostazione().getCodice(),
            p.getPostazione().getEdificio().getNome()
        ));
    }

    private void eliminaPrenotazione() {
        System.out.println("\n=== Elimina Prenotazione ===");
        System.out.print("Inserisci ID prenotazione da eliminare: ");
        try {
            Long id = Long.parseLong(scanner.nextLine().trim());
            prenotazioneService.deletePrenotazione(id, currentUser);
            System.out.println("Prenotazione eliminata con successo.");
        } catch (NumberFormatException e) {
            System.out.println("Errore: ID non valido");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void mostraListaPostazioni() {
        System.out.println("\n=== Lista Postazioni ===");
        List<Postazione> postazioni = prenotazioneService.getAllPostazioni();
        if (postazioni.isEmpty()) {
            System.out.println("Nessuna postazione presente.");
            return;
        }

        System.out.println("\nCodice     | Tipo        | Max Occ. | Edificio");
        System.out.println("-".repeat(60));
        postazioni.forEach(p -> System.out.printf("%-10s | %-11s | %-8d | %s%n",
            p.getCodice(),
            p.getTipo(),
            p.getNumeroMassimoOccupanti(),
            p.getEdificio().getNome()
        ));
    }

    private void aggiungiPostazione() {
        System.out.println("\n=== Aggiungi Postazione ===");
        try {
            System.out.print("Codice: ");
            String codice = scanner.nextLine().trim();
            
            System.out.print("Descrizione: ");
            String descrizione = scanner.nextLine().trim();
            
            System.out.print("Tipo (PRIVATO/OPENSPACE/SALA_RIUNIONI): ");
            TipoPostazione tipo = getTipoPostazione();
            
            System.out.print("Numero massimo occupanti: ");
            int maxOccupanti = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("ID Edificio: ");
            Long edificioId = Long.parseLong(scanner.nextLine().trim());
            
            prenotazioneService.addPostazione(codice, descrizione, tipo, maxOccupanti, edificioId);
            System.out.println("Postazione aggiunta con successo.");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void eliminaPostazione() {
        System.out.println("\n=== Elimina Postazione ===");
        System.out.print("Inserisci codice postazione da eliminare: ");
        String codice = scanner.nextLine().trim();
        
        try {
            prenotazioneService.deletePostazione(codice);
            System.out.println("Postazione eliminata con successo.");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void modificaPrenotazione() {
        System.out.println("\n=== Modifica Prenotazione ===");
        mostraTuttePrenotazioni();
        
        System.out.print("\nInserisci ID prenotazione da modificare (0 per annullare): ");
        try {
            Long id = Long.parseLong(scanner.nextLine().trim());
            if (id == 0) return;
            
            Prenotazione prenotazione = prenotazioneService.getPrenotazioneById(id);
            if (prenotazione == null) {
                System.out.println("Prenotazione non trovata.");
                return;
            }

            System.out.println("\nModifica data (YYYY-MM-DD) o premi INVIO per mantenere " + 
                             prenotazione.getData() + ": ");
            String nuovaData = scanner.nextLine().trim();
            
            System.out.println("Modifica codice postazione o premi INVIO per mantenere " + 
                             prenotazione.getPostazione().getCodice() + ": ");
            String nuovoCodice = scanner.nextLine().trim();
            
            prenotazioneService.modificaPrenotazione(id, 
                nuovaData.isEmpty() ? null : LocalDate.parse(nuovaData),
                nuovoCodice.isEmpty() ? null : nuovoCodice,
                currentUser);
                
            System.out.println("Prenotazione modificata con successo!");
            
        } catch (DateTimeParseException e) {
            System.out.println("Formato data non valido!");
        } catch (NumberFormatException e) {
            System.out.println("ID non valido!");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void logout() {
        currentUser = null;
        isAdmin = false;
        System.out.println("\nLogout effettuato con successo!");
    }
} 