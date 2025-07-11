package Folkestad.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import Folkestad.Project.ScraperStart;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ScheduledScraper {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledScraper.class);

    @Autowired
    private ScraperStart scraperStart;

    @PostConstruct
    public void runScraperOnStartup() {
        logger.info("Kjører scraper ved oppstart...");
        try {
            //scraperStart.startScraping();
            logger.info("Scraper ved oppstart fullført.");
        } catch (Exception e) {
            logger.error("Feil ved oppstart av scraper: ", e);
        }
    }

    @Scheduled(fixedRate = 28800000) // 8 timer i millisekunder
    public void runScraper() {
        logger.info("Kjører planlagt scraper...");
        try {
           // scraperStart.startScraping();
            logger.info("Planlagt scraper fullført.");
        } catch (Exception e) {
            logger.error("Feil ved planlagt scraping: ", e);
        }
    }
}