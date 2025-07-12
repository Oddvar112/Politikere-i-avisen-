package folkestad.project;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * VGScraper is a specialized Scraper for extracting articles from VG frontpage.
 * <p>
 * It efficiently processes articles and extracts person names with their associated article links.
 * </p>
 */
public class VGScraper extends Scraper {

    private final IsVgArticlePredicate articlePredicate = new IsVgArticlePredicate();

    /**
     * Constructs a new VGScraper for the given URL.
     * @param url the URL to scrape
     */
    public VGScraper(final String url) {
        super(url);
    }

    /**
     * Extracts all article links from VG frontpage by scraping article elements under main.
     * @param doc the frontpage document
     * @return list of article links
     */
    private ArrayList<String> getAllLinksFromFrontpage(final Document doc) {
        Elements articles = doc.select("main article");
        ArrayList<String> articleLinks = new ArrayList<>();
        
        for (Element article : articles) {
            Elements links = article.select("a[href]");
            
            for (Element link : links) {
                String href = link.attr("href");
                String absoluteUrl = link.attr("abs:href");
                
                if (href != null && !href.trim().isEmpty() && !href.startsWith("#")) {
                    articleLinks.add(absoluteUrl);
                }
            }
        }
        
        return articleLinks.stream()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Extracts the full text from a VG article by focusing on main content area.
     * @param doc the article document
     * @return the concatenated text from main article content
     */
    @Override
    public String getAllText(final Document doc) {
        StringBuilder text = new StringBuilder();
        
        Elements mainContent = doc.select("main");
        if (mainContent.isEmpty()) {
            return super.getAllText(doc);
        }
        
        Elements headlines = mainContent.select("heading, h1, h2");
        if (!headlines.isEmpty()) {
            text.append(headlines.text()).append(" ");
        }
        
        Elements paragraphs = mainContent.select("paragraph, p");
        if (!paragraphs.isEmpty()) {
            text.append(paragraphs.text()).append(" ");
        }
        
        Elements otherText = mainContent.select("sectionheader, time");
        for (Element element : otherText) {
            String elementText = element.text();
            if (!elementText.toLowerCase().contains("annonse") && 
                !elementText.toLowerCase().contains("reklame") &&
                !elementText.toLowerCase().contains("lytt til") &&
                elementText.length() > 10) {
                text.append(elementText).append(" ");
            }
        }
        
        String result = text.toString().trim();
        
        if (result.length() < 100) {
            return super.getAllText(doc);
        }
        
        return result;
    }

    /**
     * Effektiv metode som henter artikler og bygger person-artikkel-indeks i én operasjon.
     * Dette unngår å koble seg opp til samme artikkel flere ganger.
     * @param extractor NorwegianNameExtractor-instans
     * @return PersonArticleIndex med alle personer og hvilke artikler de er nevnt i
     */
    public PersonArticleIndex buildPersonArticleIndexEfficient(final NorwegianNameExtractor extractor) {
        PersonArticleIndex index = new PersonArticleIndex();

        ArrayList<String> allLinks = getAllLinksFromFrontpage(super.connectToSite(getUrl()));

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