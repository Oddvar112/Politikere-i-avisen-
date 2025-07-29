package folkestad.project.scrapers;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import folkestad.project.PersonArticleIndex;
import folkestad.project.extractors.NorwegianNameExtractor;
import folkestad.project.predicates.IsVgArticlePredicate;

/**
 * VGScraper is a specialized Scraper for extracting articles from VG frontpage.
 * <p>
 * It efficiently processes articles and extracts person names with their
 * associated article links.
 * </p>
 */
public class VGScraper extends Scraper {

    private final IsVgArticlePredicate articlePredicate = new IsVgArticlePredicate();

    /**
     * Constructs a new VGScraper for the given URL.
     * 
     * @param url the URL to scrape
     */
    public VGScraper(final ArrayList<String> urls) {
        super(urls);
    }

    /**
     * Extracts all article links from VG frontpage by scraping article elements
     * under main.
     * 
     * @param doc the frontpage document
     * @return list of article links
     */
    @Override
    protected ArrayList<String> getlinksFrompage(Document doc) {
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


    @Override
    public String getAllText(final Document doc) {
        StringBuilder text = new StringBuilder();
        StringBuilder xigzwText = new StringBuilder(); //tags på siden 

        Elements mainContent = doc.select("main");

        Elements publishedElements = mainContent.select("*:contains(Publisert)");
        int totalPublishedCount = 0;
        for (Element pub : publishedElements) {
            String pubText = pub.ownText().trim();
            if (pubText.contains("Publisert") || pubText.startsWith("Publisert")) {
                totalPublishedCount++;
            }
        }

        Elements skipContainers = mainContent.select(
                "[class*=reference], " +
                        "[class*=related], " +
                        "[class*=recommendation], " +
                        "[class*=button], " +
                        "[class*=controls], " +
                        "[class*=player], " +
                        "[class*=perspective], " +
                        "[class*=astro-island]");

        Elements xigzwElements = mainContent.select("[class*=item][class*=xigzw]");

        Elements headlines = mainContent.select("sectionheader, heading, h1");
        if (!headlines.isEmpty()) {
            text.append(headlines.text()).append(" ");
        }

        Elements allElements = mainContent.select("*");
        int publishedFound = 0;

        for (Element element : allElements) {
            String tagName = element.tagName();
            String ownText = element.ownText().trim();

            if ((ownText.contains("Publisert") || ownText.startsWith("Publisert")) && totalPublishedCount > 0) {
                publishedFound++;
                if (publishedFound == totalPublishedCount) {
                    break;
                }
            }

            boolean isWithinSkipContainer = false;
            for (Element container : skipContainers) {
                if (container.equals(element) || isChildOf(element, container)) {
                    isWithinSkipContainer = true;
                    break;
                }
            }

            if (!isWithinSkipContainer) {
                if (tagName.matches("paragraph|p|sectionheader|time") && !ownText.isEmpty()) {
                    if (isValidText(ownText)) {
                        text.append(ownText).append(" ");
                    }
                }
            }
        }

        for (Element xigzwEl : xigzwElements) {
            String xigzwElementText = xigzwEl.text().trim();
            if (!xigzwElementText.isEmpty()) {
                xigzwText.append(xigzwElementText).append(" ");
            }
        }

        if (xigzwText.length() > 0) {
            text.append(" ").append(xigzwText.toString());
        }

        return text.toString().trim();
    }


    private boolean isValidText(String text) {
        if (text == null) {
            return false;
        }

        String lowerText = text.toLowerCase();
        return !lowerText.contains("annonse") &&
                !lowerText.contains("reklame") &&
                !lowerText.contains("lytt til") &&
                !lowerText.contains("les også") &&
                !lowerText.contains("se også") &&
                !lowerText.contains("relaterte artikler") &&
                !lowerText.contains("anbefalte artikler") &&
                !lowerText.contains("play button");
                
            }

    private boolean isChildOf(Element element, Element container) {
        Element parent = element.parent();
        while (parent != null) {
            if (parent.equals(container)) {
                return true;
            }
            parent = parent.parent();
        }
        return false;
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
}