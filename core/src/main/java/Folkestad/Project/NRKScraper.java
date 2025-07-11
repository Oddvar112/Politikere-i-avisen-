package folkestad.project;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
    private ArrayList<String> getAllLinksFromRss(final Document doc) {
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
        Elements body = doc.select(".article-body");
        Elements intro = doc.select("header>div>p");
        Elements headline = doc.select("header>h1");
        return headline.text() + " " + intro.text() + " " + body.text();
    }

    /**
     * Effektiv metode som henter artikler og bygger person-artikkel-indeks i én operasjon.
     * Dette unngår å koble seg opp til samme artikkel flere ganger.
     * @param extractor NorwegianNameExtractor-instans
     * @return PersonArticleIndex med alle personer og hvilke artikler de er nevnt i
     */
    public PersonArticleIndex buildPersonArticleIndexEfficient(final NorwegianNameExtractor extractor) {
        PersonArticleIndex index = new PersonArticleIndex();

        // Hent alle lenker fra RSS-feed
        ArrayList<String> allLinks = getAllLinksFromRss(super.connectToSite(getUrl()));

        // Filtrer og prosesser artikler i én operasjon
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
