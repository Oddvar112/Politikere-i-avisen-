package Folkestad.Project;

import java.util.Set;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import Folkestad.Person;
import Folkestad.PersonRepository;
import Folkestad.PersonLink;

@SpringBootApplication
@Component
public class ScraperStart {

    @Autowired
    private PersonRepository personRepository;

    public void startScraping() {
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

        for (String name : names) {
            Person person = new Person();
            person.setName(name);

            PersonLink personLink = new PersonLink();
            personLink.setLink("EksempelLinkFor" + name);
            personLink.setPerson(person);

            person.getLinks().add(personLink);
            personRepository.save(person);
        }

        System.out.println("Personer lagret i databasen.");
    }
}
