package Folkestad.Project;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // Scrape NRK-forsiden og vis folk nevnt
        String url = "https://www.nrk.no/toppsaker.rss";
        NRKScraper scraper = new NRKScraper(url);
        scraper.startScraping();
        String tekst = scraper.getTekst();
        NorwegianNameExtractor extractor = new NorwegianNameExtractor();
        Set<String> names = extractor.extractNames(tekst);
        System.out.println("Folk nevnt på NRK-forsiden:");
        for (String name : names) {
            System.out.println(name);
        }
    }
}
