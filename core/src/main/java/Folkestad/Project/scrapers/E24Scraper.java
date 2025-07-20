package folkestad.project.scrapers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import folkestad.project.PersonArticleIndex;
import folkestad.project.extractors.NorwegianNameExtractor;
import folkestad.project.predicates.IsE24ArticlePredicate;

/**
 * E24Scraper is a specialized Scraper for extracting articles from E24 frontpage.
 * <p>
 * It efficiently processes articles and extracts person names with their associated article links.
 * </p>
 */
public class E24Scraper extends Scraper {

    private final IsE24ArticlePredicate articlePredicate = new IsE24ArticlePredicate();

    /**
     * Constructs a new E24Scraper for the given URL.
     * @param url the URL to scrape
     */
    public E24Scraper(final String url) {
        super(url);
    }

    /**
     * Extracts all article links from E24 frontpage by scraping link elements under main.
     * Based on actual DOM structure: main → link elements directly
     * @param doc the frontpage document
     * @return list of article links
     */
    @Override
    protected ArrayList<String> getLinks(final Document doc) {
        Elements links = doc.select("main > link[url]");
        ArrayList<String> articleLinks = new ArrayList<>();
        
        for (Element link : links) {
            String url = link.attr("url");
            
            if (url != null && !url.trim().isEmpty()) {
                if (!url.startsWith("http")) {
                    url = "https://e24.no" + (url.startsWith("/") ? url : "/" + url);
                }
                articleLinks.add(url);
            }
        }
        
        Elements traditionalLinks = doc.select("main a[href]");
        
        for (Element link : traditionalLinks) {
            String href = link.attr("href");
            String absoluteUrl = link.attr("abs:href");
            
            if (href != null && !href.trim().isEmpty() && !href.startsWith("#")) {
                articleLinks.add(absoluteUrl);
            }
        }
        
        return articleLinks.stream()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Extracts the full text from an E24 article by focusing on main content area.
     * Based on DOM structure: main → heading + paragraph elements
     * @param doc the article document
     * @return the concatenated text from main article content
     */


@Override
public String getAllText(final Document doc) {
    StringBuilder text = new StringBuilder();
    Elements articleContent = doc.select("article");
    if (articleContent.isEmpty()) {
        return super.getAllText(doc);
    }
    articleContent.select("[role=region]").remove(); 
    articleContent.select("h2:contains(Kortversjonen)").remove();
    articleContent.select("[data-test-tag*=teaser]").remove(); 
    articleContent.select("a[href*='/']").remove(); 
    articleContent.select(".advertory-e24-netboard-wrapper").remove(); 
    articleContent.select("[id*=netboard]").remove();
    articleContent.select("em").remove();
    Elements paragraphs = articleContent.select("p");    
    for (Element paragraph : paragraphs) {
        String textContent = paragraph.text().trim();
        text.append(textContent).append(" ");
    }
    
    return text.toString().trim();
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
