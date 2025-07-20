package folkestad.project.scrapers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Predicate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import folkestad.project.PersonArticleIndex;
import folkestad.project.extractors.NorwegianNameExtractor;
import folkestad.project.TextSummarizer.TextSummarizer;
import folkestad.project.TextSummarizer.SummaryResult;
import folkestad.Innlegg;
import folkestad.InnleggRepository;

/**
 * Base class for web scrapers using Jsoup.
 * Nå med integrert text summarization og lagring av sammendrag.
 */
public abstract class Scraper {
    private Document doc;
    private String tekst;
    private String url;

    private InnleggRepository innleggRepository;

    private final TextSummarizer textSummarizer = new TextSummarizer();

    /**
     * Constructs a new Scraper for the given URL.
     * 
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
     * 
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
     * 
     * @return the Document
     */
    public Document getDoc() {
        return this.doc;
    }

    /**
     * Extracts all text from the given Jsoup Document.
     * 
     * @param doc the Document to extract text from
     * @return the extracted text
     */
    public String getAllText(final Document doc) {
        return doc.text();
    }

    /**
     * Returns the collected text from the scraping process.
     * 
     * @return the collected text
     */
    public String getTekst() {
        return tekst;
    }

    /**
     * Returns the URL this scraper is set to scrape.
     * 
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Abstract method that subclasses must implement to get links from their
     * source.
     * 
     * @param doc the source document (RSS feed, frontpage, etc.)
     * @return list of article links
     */
    protected abstract ArrayList<String> getLinks(Document doc);

    /**
     * Prosesserer og lagrer sammendrag av en artikkel.
     * 
     * @param articleUrl   URL til artikkelen
     * @param originalText den fulle artikkelteksten
     */
    protected void processAndSaveSummary(String articleUrl, String originalText) {
        if (innleggRepository != null && innleggRepository.existsByLink(articleUrl)) {
            return;
        }

        SummaryResult summaryResult = textSummarizer.summarize(originalText);
        if (summaryResult.getSummaryWordCount() > 10 &&
                summaryResult.getCompressionRatio() < 0.8) {

            Innlegg innlegg = new Innlegg();
            innlegg.setLink(articleUrl);
            innlegg.setSammendragWithStats(summaryResult.getSummary(), originalText);

            if (innleggRepository != null) {
                innleggRepository.save(innlegg);
            }
        }
    }

    /**
     * Effektiv metode som henter artikler og bygger person-artikkel-indeks i én
     * operasjon.
     * Nå med integrert sammendrag-generering og lagring.
     * 
     * @param extractor        NorwegianNameExtractor-instans
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

                    // Prosesser og lagre sammendrag asynkront
                    processAndSaveSummary(articleUrl, text);

                    // Fortsett med navnekstrahering som før
                    Set<String> names = extractor.extractNames(text);
                    index.addMentions(names, articleUrl);
                });

        return index;
    }

    public void setInnleggRepository(InnleggRepository innleggRepository) {
        this.innleggRepository = innleggRepository;
    }
}