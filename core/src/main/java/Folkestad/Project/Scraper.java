package Folkestad.Project;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Base class for web scrapers using Jsoup.
 * <p>
 * Provides basic functionality for connecting to a web page and extracting text.
 * </p>
 */
public class Scraper {

    private Document doc;
    private String tekst;
    protected String url;

    /**
     * Constructs a new Scraper for the given URL.
     * @param url the URL to scrape
     */
    public Scraper(String url) {
        this.url = url;
    }

    /**
     * Starts the scraping process and collects all text from the main URL.
     */
    public void startScraping() {
        this.tekst = fåAllTekst(ConnectTilSide(url));
    }

    /**
     * Connects to the given URL and returns the parsed Jsoup Document.
     * @param url the URL to connect to
     * @return the parsed Document
     * @throws RuntimeException if the connection fails
     */
    protected Document ConnectTilSide(String url) {
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
    public String fåAllTekst(Document doc) {
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
}