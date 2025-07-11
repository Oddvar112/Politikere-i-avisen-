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

/**
 * Component responsible for scraping NRK articles and extracting person names.
 * This class orchestrates the entire scraping process from fetching articles
 * to saving person-article relationships in the database.
 */
@Component
public final class ScraperStart {

    @Autowired
    private PersonRepository personRepository;

    /**
     * Starts the scraping process for NRK articles.
     * Extracts person names from articles and saves them with their associated article links.
     * Uses efficient database operations to minimize queries and optimize performance.
     */
    public void startScraping() {
        String url = "https://www.nrk.no/toppsaker.rss";
        NRKScraper scraper = new NRKScraper(url);

        NorwegianNameExtractor extractor = new NorwegianNameExtractor();

        PersonArticleIndex personArticleIndex = scraper.buildPersonArticleIndexEfficient(extractor);

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
                    PersonLink personLink = new PersonLink();
                    personLink.setLink(articleUrl);
                    personLink.setPerson(person);
                    person.getLinks().add(personLink);
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
        extractor.close();

        System.out.println("Personer lagret i databasen med tilhørende artikkellenker. "
            + "Antall personer behandlet: " + allPersonNames.size()
            + ", Antall personer lagret/oppdatert: " + personsToSave.size());
    }
}
