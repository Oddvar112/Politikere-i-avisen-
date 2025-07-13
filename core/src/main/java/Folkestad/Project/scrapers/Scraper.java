package folkestad.project.scrapers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Predicate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import folkestad.project.PersonArticleIndex;
import folkestad.project.extractors.NorwegianNameExtractor;
/**
 * Base class for web scrapers using Jsoup.
 * <p>
 * Provides basic functionality for connecting to a web page and extracting text.
 * </p>
 */
public abstract class Scraper {
    private Document doc;
    private String tekst;
    private String url;
    /**
     * Constructs a new Scraper for the given URL.
     * @param url the URL to scrape
     */
    public Scraper(final String url) {
        this.url = url;
    }
    /**
     * Starts the scraping process and collects all text from the main URL.
     */
    public void startScraping() {
        this.tekst = getAllText(connectToSite(url));
    }
    /**
     * Connects to the given URL and returns the parsed Jsoup Document.
     * @param url the URL to connect to
     * @return the parsed Document
     * @throws RuntimeException if the connection fails
     */
    protected Document connectToSite(final String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke koble til siden: " + url, e);
        }
    }
    /**
     * Returns the last fetched Jsoup Document.
     * @return the Document
     */
    public Document getDoc() {
        return this.doc;
    }
    /**
     * Extracts all text from the given Jsoup Document.
     * @param doc the Document to extract text from
     * @return the extracted text
     */
    public String getAllText(final Document doc) {
        return doc.text();
    }
    /**
     * Returns the collected text from the scraping process.
     * @return the collected text
     */
    public String getTekst() {
        return tekst;
    }
    /**
     * Returns the URL this scraper is set to scrape.
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Abstract method that subclasses must implement to get links from their source.
     * @param doc the source document (RSS feed, frontpage, etc.)
     * @return list of article links
     */
    protected abstract ArrayList<String> getLinks(Document doc);

    /**
     * Effektiv metode som henter artikler og bygger person-artikkel-indeks i én operasjon.
     * Dette unngår å koble seg opp til samme artikkel flere ganger.
     * @param extractor NorwegianNameExtractor-instans
     * @param articlePredicate predicate for å filtrere ut kun ekte artikler
     * @return PersonArticleIndex med alle personer og hvilke artikler de er nevnt i
     */
    public PersonArticleIndex buildPersonArticleIndexEfficient(final NorwegianNameExtractor extractor, 
                                                              final Predicate<Document> articlePredicate) {
        PersonArticleIndex index = new PersonArticleIndex();

        ArrayList<String> allLinks = getLinks(connectToSite(getUrl()));

        allLinks.parallelStream()
            .map(this::connectToSite)
            .filter(articlePredicate)
            .forEach(doc -> {
                String articleUrl = doc.location();
                String text = getAllText(doc);
                Set<String> names = extractor.extractNames(text);
                index.addMentions(names, articleUrl);
            });

        return index;
    }
}
