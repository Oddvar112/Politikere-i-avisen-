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
import folkestad.Nettsted;

/**
 * Komponent ansvarlig for skraping av NRK-artikler og utvinning av personnavn.
 * Bruker nå navnbaserte primærnøkler for kandidater, som eliminerer duplikatproblemer.
 */
@Component
public final class ScraperStart {

    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private KandidatNameExtractor kandidatNameExtractor;
    
    @Autowired
    private KandidatStortingsvalgRepository kandidatRepository;
    /**
     * Starter skrapingprosessen for NRK-artikler.
     * Ekstraherer personnavn fra artikler og lagrer dem med tilhørende artikkellenker.
     */
    public void startScrapingAllNames() {
        String url = Nettsted.NRK.getRssUrl();
        NRKScraper scraper = new NRKScraper(url);
        NorwegianNameExtractor extractor = new NorwegianNameExtractor();
        PersonArticleIndex personArticleIndex = scraper.buildPersonArticleIndexEfficient(extractor);
        processAndSavePersons(personArticleIndex);
    }

    /**
     * Starter skrapingprosessen for kandidatnavn ved hjelp av navnbaserte primærnøkler.
     * Mye enklere nå som hvert navn er unikt i databasen.
     */
    public void startScrapingKandidatNames() {
        String url = Nettsted.NRK.getRssUrl();
        NRKScraper scraper = new NRKScraper(url);
        PersonArticleIndex personArticleIndex = scraper.buildPersonArticleIndexEfficient(kandidatNameExtractor);
        processAndSaveKandidater(personArticleIndex);
    }

    /**
     * Prosesserer og lagrer kandidater og deres lenker fra PersonArticleIndex.
     * Mye enklere nå med navnbaserte primærnøkler - ingen duplikathåndtering nødvendig!
     *
     * @param personArticleIndex indeksen med kandidater og deres artikler
     */
    private void processAndSaveKandidater(PersonArticleIndex personArticleIndex) {
        Set<String> allKandidatNames = personArticleIndex.getAllPersons();
        List<KandidatStortingsvalg> existingKandidatList = kandidatRepository.findAllWithLinks();
        Map<String, KandidatStortingsvalg> existingKandidatMap = existingKandidatList
                .stream()
                .collect(Collectors.toMap(KandidatStortingsvalg::getNavn, kandidat -> kandidat));
        
        List<KandidatStortingsvalg> kandidaterToSave = new ArrayList<>();
        for (String kandidatName : allKandidatNames) {
            KandidatStortingsvalg kandidat = existingKandidatMap.get(kandidatName);
            if (kandidat != null) { // Kandidat finnes i databasen
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
        
        if (!kandidaterToSave.isEmpty()) {
            kandidatRepository.saveAll(kandidaterToSave);
        }
    }

    /**
     * Felles metode for å prosessere og lagre personer fra PersonArticleIndex.
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
                    person.addLink(personLink);
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