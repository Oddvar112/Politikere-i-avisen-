package Folkestad.Project;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * NRKScraper is a specialized Scraper for extracting articles from NRK RSS feeds and article pages.
 * <p>
 * It filters out non-article links and collects the full text (headline, intro, and body) from each article.
 * </p>
 */
public class NRKScraper extends Scraper {

    private String tekst;
    private final IsNrkArticlePredicate articlePredicate = new IsNrkArticlePredicate();

    /**
     * Constructs a new NRKScraper for the given URL.
     * @param url the URL to scrape
     */
    public NRKScraper(String url) {
        super(url);
    }

    /**
     * Starts the scraping process and collects all article text from filtered links.
     */
    @Override
    public void startScraping() {
        collectAllText(filterForArticles(getAllLinksFromRss(super.ConnectTilSide(url))));
    }

    /**
     * Filters the provided list of links, keeping only those that are NRK articles.
     * @param allLinks all links from the RSS feed
     * @return filtered list of article links
     */
    private ArrayList<String> filterForArticles(ArrayList<String> allLinks) {
        return allLinks.stream()
                .map(this::ConnectTilSide)
                .filter(articlePredicate)
                .map(Document::location)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Extracts all article links from an RSS feed document.
     * @param doc the RSS feed document
     * @return list of article links
     */
    public ArrayList<String> getAllLinksFromRss(Document doc) {
        Elements links = doc.select("item > link");
        return links.stream().map(link -> link.text()).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Extracts the full text (headline, intro, and body) from an article document.
     * @param doc the article document
     * @return the concatenated text
     */
    @Override
    public String fåAllTekst(Document doc) {
        Elements body = doc.select(".article-body");
        Elements intro = doc.select("header>div>p");
        Elements headline = doc.select("header>h1");
        return headline.text() + " " + intro.text() + " " + body.text();
    }

    /**
     * Collects all text from the filtered article links and joins them with two newlines.
     * @param filteredLinks the filtered article links
     */
    public void collectAllText(ArrayList<String> filteredLinks) {
        this.tekst = filteredLinks.parallelStream()
                .map(link -> fåAllTekst(ConnectTilSide(link)))
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * Returns the collected text from all articles.
     * @return the collected text
     */
    @Override
    public String getTekst() {
        return tekst;
    }

    /**
     * Bygger en person-artikkel-indeks for en liste av artikler.
     * @param articleLinks Liste med artikkellenker
     * @param extractor NorwegianNameExtractor-instans
     * @return PersonArticleIndex med alle personer og hvilke artikler de er nevnt i
     */
    public PersonArticleIndex buildPersonArticleIndex(ArrayList<String> articleLinks, NorwegianNameExtractor extractor) {
        PersonArticleIndex index = new PersonArticleIndex();
        articleLinks.parallelStream().forEach(link -> {
            Document doc = ConnectTilSide(link);
            String text = fåAllTekst(doc);
            Set<String> names = extractor.extractNames(text);
            index.addMentions(names, link);
        });
        return index;
    }
}
