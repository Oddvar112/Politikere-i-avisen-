package Folkestad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main class for the Server Application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = {"Folkestad"})
@ComponentScan(basePackages = {"Folkestad.Project", "Folkestad.server", "Folkestad.dto", "Folkestad.model"})
@EntityScan("Folkestad.model")
@EnableJpaRepositories("Folkestad.model")
@EnableScheduling // Aktiverer planlegging av oppgaver
public class Main {
    /**
     * The main method which serves as the entry point for the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(final String[] args) {
        SpringApplication.run(Main.class, args);
    }
}