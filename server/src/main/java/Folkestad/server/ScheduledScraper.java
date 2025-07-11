package folkestad.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import folkestad.project.ScraperStart;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public final class ScheduledScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledScraper.class);

    @Autowired
    private ScraperStart scraperStart;

    /**
     * Kjører scraper ved oppstart av applikasjonen.
     * Metoden er merket med @PostConstruct og kjøres automatisk etter at
     * Spring har instansiert og konfigurert denne klassen.
     */
    @PostConstruct
    public void runScraperOnStartup() {
        LOGGER.info("Kjører scraper ved oppstart...");
        try {
            //scraperStart.startScraping();
            LOGGER.info("Scraper ved oppstart fullført.");
        } catch (Exception e) {
            LOGGER.error("Feil ved oppstart av scraper: ", e);
        }
    }

    /**
     * Kjører planlagt scraper med fast intervall.
     * Metoden er merket med @Scheduled og kjøres automatisk hver 8. time.
     * Dette sikrer at dataene holdes oppdatert ved jevnlige intervaller.
     */
    @Scheduled(fixedRate = 28800000) // 8 timer i millisekunder
    public void runScraper() {
        LOGGER.info("Kjører planlagt scraper...");
        try {
           // scraperStart.startScraping();
            LOGGER.info("Planlagt scraper fullført.");
        } catch (Exception e) {
            LOGGER.error("Feil ved planlagt scraping: ", e);
        }
    }
}
