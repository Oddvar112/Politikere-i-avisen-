package folkestad.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import folkestad.project.ScraperStart;
import folkestad.project.analysis.KandidateAnalysis;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public final class ScheduledScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledScraper.class);

    @Autowired
    private ScraperStart scraperStart;
    
    @Autowired
    private KandidateAnalysis kandidateAnalysis;

    /**
     * Kjører scraper ved oppstart av applikasjonen.
     * Metoden er merket med @PostConstruct og kjøres automatisk etter at
     * Spring har instansiert og konfigurert denne klassen.
     */
    @PostConstruct
    public void runScraperOnStartup() {
        LOGGER.info("=== Starter scraper ved oppstart ===");
        try {
            LOGGER.info("Initialiserer candidate name extractor...");
            long startTime = System.currentTimeMillis();


            
            scraperStart.startScrapingKandidatNames();
            
            LOGGER.info("Starter caching av analyse data...");
            kandidateAnalysis.analyzeKandidatData();
            LOGGER.info("Caching av analyse data fullført");
            
            long endTime = System.currentTimeMillis();
            LOGGER.info("=== Scraper fullført på {} ms ===", (endTime - startTime));
            
        } catch (Exception e) {
            LOGGER.error("KRITISK FEIL ved oppstart av scraper: ", e);
            // Log stacktrace også
            LOGGER.error("Stacktrace: ", e);
        }
        LOGGER.info("=== PostConstruct metode fullført ===");
    }

    /**
     * Kjører planlagt scraper med fast intervall.
     * Metoden er merket med @Scheduled og kjøres automatisk hver 8. time.
     * Dette sikrer at dataene holdes oppdatert ved jevnlige intervaller.
     */
    @Scheduled(fixedRate = 28800000) // 8 timer i millisekunder
    public void runScraper() {
        LOGGER.info("=== Starter planlagt scraper ===");
        try {
            long startTime = System.currentTimeMillis();
            scraperStart.startScrapingKandidatNames();
            
            LOGGER.info("Starter caching av analyse data...");
            kandidateAnalysis.analyzeKandidatData();
            LOGGER.info("Caching av analyse data fullført");
            
            long endTime = System.currentTimeMillis();
            LOGGER.info("=== Planlagt scraper fullført på {} ms ===", (endTime - startTime));
        } catch (Exception e) {
            LOGGER.error("Feil ved planlagt scraping: ", e);
        }
    }
}