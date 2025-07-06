package Folkestad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for the Server Application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = {"Folkestad"})
@EntityScan("Folkestad")
@EnableJpaRepositories("Folkestad")
@EnableScheduling
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    /**
     * The main method which serves as the entry point for the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(final String[] args) {
        logger.info("Starter applikasjonen med scheduling aktivert...");
        SpringApplication.run(Main.class, args);
    }
}