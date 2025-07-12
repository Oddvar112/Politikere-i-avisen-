package folkestad.project;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import folkestad.Person;
import folkestad.PersonRepository;
import folkestad.PersonLink;
import folkestad.KandidatStortingsvalg;
import folkestad.KandidatStortingsvalgRepository;
import folkestad.KandidatLink;
import folkestad.KandidatLinkRepository;
import folkestad.Nettsted;

/**
 * Component responsible for scraping NRK articles and extracting person names.
 * This class orchestrates the entire scraping process from fetching articles
 * to saving person-article relationships in the database.
 */
@Component
public final class ScraperStart {

    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private KandidatNameExtractor kandidatNameExtractor;
    
    @Autowired
    private KandidatStortingsvalgRepository kandidatRepository;
    
    @Autowired
    private KandidatLinkRepository kandidatLinkRepository;

    /**
     * Starts the scraping process for NRK articles.
     * Extracts person names from articles and saves them with their associated article links.
     * Uses efficient database operations to minimize queries and optimize performance.
     */
    public void startScrapingAllNames() {
        String url = Nettsted.NRK.getRssUrl();
        NRKScraper scraper = new NRKScraper(url);
        NorwegianNameExtractor extractor = new NorwegianNameExtractor();
        PersonArticleIndex personArticleIndex = scraper.buildPersonArticleIndexEfficient(extractor);
        processAndSavePersons(personArticleIndex);
    }

    /**
     * Starts the scraping process for NRK articles using KandidatNameExtractor.
     * Extracts only candidate names from articles and saves them with their associated article links.
     * This is more efficient as it only processes politicians/candidates from the database.
     */
    public void startScrapingKandidatNames() {
        System.out.println("[DEBUG] Starting startScrapingKandidatNames...");
        
        String url = Nettsted.NRK.getRssUrl();
        System.out.println("[DEBUG] RSS URL: " + url);
        
        NRKScraper scraper = new NRKScraper(url);
        System.out.println("[DEBUG] Created NRKScraper, about to build PersonArticleIndex...");
        
        PersonArticleIndex personArticleIndex = scraper.buildPersonArticleIndexEfficient(kandidatNameExtractor);
        System.out.println("[DEBUG] Built PersonArticleIndex successfully!");
        System.out.println("[DEBUG] PersonArticleIndex has " + personArticleIndex.getAllPersons().size() + " persons");
        
        System.out.println("[DEBUG] About to call processAndSaveKandidater...");
        processAndSaveKandidater(personArticleIndex);
        System.out.println("[DEBUG] startScrapingKandidatNames completed!");
    }

    /**
     * Prosesserer og lagrer kandidater og deres lenker fra PersonArticleIndex.
     * Spesialisert for kandidater som bruker KandidatLink.
     *
     * @param personArticleIndex indeksen med kandidater og deres artikler
     */
    private void processAndSaveKandidater(PersonArticleIndex personArticleIndex) {
        System.out.println("[DEBUG] Starting processAndSaveKandidater...");
        
        Set<String> allKandidatNames = personArticleIndex.getAllPersons();
        System.out.println("[DEBUG] Found " + allKandidatNames.size() + " persons in PersonArticleIndex");
        
        // Hent eksisterende kandidater fra database
        System.out.println("[DEBUG] About to fetch existing kandidater from database with links...");
        Map<String, KandidatStortingsvalg> existingKandidatMap = kandidatRepository.findAllWithLinks()
                .stream()
                .collect(Collectors.toMap(KandidatStortingsvalg::getNavn, kandidat -> kandidat));
        System.out.println("[DEBUG] Found " + existingKandidatMap.size() + " existing kandidater in database");
        
        List<KandidatStortingsvalg> kandidaterToSave = new ArrayList<>();
        
        System.out.println("[DEBUG] Processing each kandidat name...");
        int processedCount = 0;
        for (String kandidatName : allKandidatNames) {
            processedCount++;
            if (processedCount % 10 == 0) {
                System.out.println("[DEBUG] Processed " + processedCount + " out of " + allKandidatNames.size() + " kandidat names");
            }
            
            KandidatStortingsvalg kandidat = existingKandidatMap.get(kandidatName);
            if (kandidat != null) { // Bare behandle eksisterende kandidater
                Set<String> articleUrlsForKandidat = personArticleIndex.getArticlesForPerson(kandidatName);
                Set<String> existingLinks = kandidat.getLinks().stream()
                        .map(KandidatLink::getLink)
                        .collect(Collectors.toSet());
                boolean hasNewLinks = false;
                for (String articleUrl : articleUrlsForKandidat) {
                    if (!existingLinks.contains(articleUrl)) {
                        KandidatLink kandidatLink = KandidatLink.createWithDetectedNettsted(articleUrl, kandidat);
                        kandidat.addLink(kandidatLink);
                        hasNewLinks = true;
                    }
                }
                if (hasNewLinks) {
                    kandidaterToSave.add(kandidat);
                }
            }
        }
        
        System.out.println("[DEBUG] Finished processing all kandidat names. " + kandidaterToSave.size() + " kandidater to save");
        
        if (!kandidaterToSave.isEmpty()) {
            System.out.println("[DEBUG] About to save " + kandidaterToSave.size() + " kandidater...");
            kandidatRepository.saveAll(kandidaterToSave);
            System.out.println("Lagret " + kandidaterToSave.size() + " kandidater med nye lenker.");
        }
        
        System.out.println("[DEBUG] processAndSaveKandidater completed successfully!");
    }

    /**
     * Felles metode for å prosessere og lagre personer fra PersonArticleIndex.
     * Unngår duplisert kode mellom de forskjellige scraping-metodene.
     *
     * @param personArticleIndex indeksen med personer og deres artikler
     */
    private void processAndSavePersons(PersonArticleIndex personArticleIndex) {
        Set<String> allPersonNames = personArticleIndex.getIndex().keySet();
        List<Person> existingPersons = personRepository.findByNameInWithLinks(new ArrayList<>(allPersonNames));
        Map<String, Person> existingPersonMap = existingPersons.stream()
                .collect(Collectors.toMap(Person::getName, person -> person));
        List<Person> personsToSave = new ArrayList<>();
        
        for (String personName : allPersonNames) {
            Person person = existingPersonMap.getOrDefault(personName, new Person());
            if (person.getName() == null) {
                person.setName(personName);
            }
            Set<String> articleUrlsForPerson = personArticleIndex.getArticlesForPerson(personName);
            Set<String> existingLinks = person.getLinks().stream()
                    .map(PersonLink::getLink)
                    .collect(Collectors.toSet());
            boolean hasNewLinks = false;
            for (String articleUrl : articleUrlsForPerson) {
                if (!existingLinks.contains(articleUrl)) {
                    PersonLink personLink = PersonLink.createWithDetectedNettsted(articleUrl, person);
                    person.addLink(personLink);  // Använder den nya metoden för korrekt synkronisering
                    hasNewLinks = true;
                }
            }
            if (person.getId() == null || hasNewLinks) {
                personsToSave.add(person);
            }
        }
        
        if (!personsToSave.isEmpty()) {
            personRepository.saveAll(personsToSave);
        }
    }
}

