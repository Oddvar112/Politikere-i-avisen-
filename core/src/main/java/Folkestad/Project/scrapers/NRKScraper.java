package folkestad.project.scrapers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import folkestad.project.PersonArticleIndex;
import folkestad.project.extractors.NorwegianNameExtractor;
import folkestad.project.predicates.IsNrkArticlePredicate;

/**
 * NRKScraper is a specialized Scraper for extracting articles from NRK RSS feeds and article pages.
 * <p>
 * It efficiently processes articles and extracts person names with their associated article links.
 * </p>
 */
public class NRKScraper extends Scraper {

    private final IsNrkArticlePredicate articlePredicate = new IsNrkArticlePredicate();

    /**
     * Constructs a new NRKScraper for the given URL.
     * @param url the URL to scrape
     */
    public NRKScraper(final String url) {
        super(url);
    }

    /**
     * Extracts all article links from an RSS feed document.
     * @param doc the RSS feed document
     * @return list of article links
     */
    @Override
    protected ArrayList<String> getLinks(final Document doc) {
        Elements links = doc.select("item > link");
        return links.stream().map(link -> link.text()).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Extracts the full text (headline, intro, and body) from an article document.
     * @param doc the article document
     * @return the concatenated text
     */
@Override
public String getAllText(final Document doc) {
    StringBuilder result = new StringBuilder();
    
    // Velg paragraphs og sectionheaders
    Elements textElements = doc.select("paragraph, p, sectionheader");
    
    for (Element element : textElements) {
        String text = element.text().trim();
        if (!text.isEmpty()) {
            result.append(text).append("\n");
        }
    }
    
    return result.toString();
}

    /**
     * Effektiv metode som henter artikler og bygger person-artikkel-indeks i én operasjon.
     * Dette unngår å koble seg opp til samme artikkel flere ganger.
     * @param extractor NorwegianNameExtractor-instans
     * @return PersonArticleIndex med alle personer og hvilke artikler de er nevnt i
     */
    public PersonArticleIndex buildPersonArticleIndexEfficient(final NorwegianNameExtractor extractor) {
        return super.buildPersonArticleIndexEfficient(extractor, articlePredicate);
    }
}
