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
 * E24Scraper is a specialized Scraper for extracting articles from E24
 * frontpage.
 * <p>
 * It efficiently processes articles and extracts person names with their
 * associated article links.
 * </p>
 */
public class E24Scraper extends Scraper {

    private final IsE24ArticlePredicate articlePredicate = new IsE24ArticlePredicate();

    /**
     * Constructs a new E24Scraper for the given URL.
     *
     * @param urls the URLs to scrape
     */
    public E24Scraper(final ArrayList<String> urls) {
        super(urls);
    }

    /**
     * Extracts the full text from an E24 article by focusing on main content area.
     * Based on DOM structure: main → heading + paragraph elements
     *
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
     * Effektiv metode som henter artikler og bygger person-artikkel-indeks i én
     * operasjon.
     * Dette unngår å koble seg opp til samme artikkel flere ganger.
     *
     * @param extractor NorwegianNameExtractor-instans
     * @return PersonArticleIndex med alle personer og hvilke artikler de er nevnt i
     */
    public PersonArticleIndex buildPersonArticleIndexEfficient(final NorwegianNameExtractor extractor) {
        return super.buildPersonArticleIndexEfficient(extractor, articlePredicate);
    }

    @Override
    /**
     * Henter alle artikkellenker fra en E24-side.
     * Hvis klassen skal utvides, må man sikre at filtrering av lenker skjer på riktig måte.
     *
     * @param doc Dokumentet som skal analyseres
     * @return Liste med artikkellenker
     */
    /**
     * Henter alle artikkellenker fra en E24-side.
     * Hvis du utvider denne klassen, må du sikre at filtrering av lenker skjer på riktig måte.
     * For sikker utvidelse, kall super.getlinksFrompage(doc) og filtrer resultatet.
     *
     * @param doc Dokumentet som skal analyseres
     * @return Liste med artikkellenker
     */
    protected ArrayList<String> getlinksFrompage(final Document doc) {
        ArrayList<String> articleLinks = new ArrayList<>();
        Elements allLinks = doc.select("a[href]");
        for (Element link : allLinks) {
            String href = link.attr("href");

            if (href != null && !href.trim().isEmpty()) {
                String fullUrl = href;
                if (!href.startsWith("http")) {
                    if (href.startsWith("/")) {
                        fullUrl = "https://e24.no" + href;
                    } else {
                        fullUrl = "https://e24.no/" + href;
                    }
                }
                articleLinks.add(fullUrl);
            }
        }
        articleLinks = articleLinks.stream()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        return articleLinks;
    }
}

