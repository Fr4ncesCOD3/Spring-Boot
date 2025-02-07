package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale dell'applicazione Spring Boot.
 * Contiene il punto di ingresso dell'applicazione e la configurazione di base.
 */
@SpringBootApplication // Abilita la configurazione automatica di Spring Boot
public class DemoApplication {

    /**
     * Metodo main che avvia l'applicazione Spring Boot.
     * Inizializza il contesto dell'applicazione e avvia il server embedded.
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        // Avvia l'applicazione Spring Boot passando la classe principale e gli argomenti
        SpringApplication.run(DemoApplication.class, args);
    }

}
