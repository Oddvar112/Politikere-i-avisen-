package Folkestad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import Folkestad.Project.ScraperStart;

import jakarta.annotation.PostConstruct;

@Component
public class ScheduledScraper {

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void runScraperOnStartup() {
        System.out.println("Kjører scraper ved oppstart...");
        ScraperStart scraperStart = context.getBean(ScraperStart.class);
        scraperStart.startScraping(); // Kall til scraping-logikken i ScraperStart
    }

    @Scheduled(fixedRate = 28800000) // 8 timer i millisekunder
    public void runScraper() {
        System.out.println("Kjører scraper...");
        ScraperStart scraperStart = context.getBean(ScraperStart.class);
        scraperStart.startScraping(); // Kall til scraping-logikken i ScraperStart
    }
}
