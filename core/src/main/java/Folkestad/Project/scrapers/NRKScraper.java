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
 * NRKScraper is a specialized Scraper for extracting articles from NRK RSS
 * feeds and article pages.
 * <p>
 * It efficiently processes articles and extracts person names with their
 * associated article links.
 * </p>
 */
public class NRKScraper extends Scraper {

    private final IsNrkArticlePredicate articlePredicate = new IsNrkArticlePredicate();

    /**
     * Constructs a new NRKScraper for the given URL.
     * 
     * @param url the URL to scrape
     */
    public NRKScraper(final ArrayList<String> urls) {
        super(urls);
    }

    /**
     * Extracts all article links from an RSS feed document.
     * 
     * @param doc the RSS feed document
     * @return list of article links
     */
    @Override
    protected ArrayList<String> getlinksFrompage(Document doc) {
        Elements links = doc.select("item > link");
        return links.stream().map(link -> link.text()).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Extracts the full text (headline, intro, and body) from an article document.
     * 
     * @param doc the article document
     * @return the concatenated text
     */
    @Override
    public String getAllText(final Document doc) {
        StringBuilder result = new StringBuilder();
        Element articleElement = doc.selectFirst("article");
        if (articleElement == null) {
            return "";
        }
        Elements publishedElements = articleElement.select("*:contains(Publisert)");
        int totalPublishedCount = 0;
        for (Element pub : publishedElements) {
            if (pub.ownText().trim().equals("Publisert")) {
                totalPublishedCount++;
            }
        }
        Elements skipContainers = articleElement.select(
                "[class*=reference], " +
                        "[class*=image], " +
                        "[class*=gallery], " +
                        "[class*=galleri], " +
                        "[class*=article-location], " +
                        ".author, " +
                        ".authors, " +
                        "[class*=article-header-sidebar], " +
                        "figure, " +
                        "[class*=dh-infosveip], " +
                        "[data-name*=dh-infosveip]");

        // Iterer gjennom innholdet i article
        Elements allElements = articleElement.select("*");
        int publishedFound = 0;

        for (Element element : allElements) {
            String tagName = element.tagName();
            String ownText = element.ownText().trim();
            String fullText = element.text().trim();

            // Sjekk om dette er et "Publisert" element (stopp-kondisjon)
            if (ownText.equals("Publisert") && totalPublishedCount > 0) {
                publishedFound++;
                if (publishedFound == totalPublishedCount) {
                    break; // Stopp ved siste "Publisert"
                }
            }

            // Sjekk om elementet ligger innenfor en container som skal hoppes over
            boolean isWithinSkipContainer = false;
            for (Element container : skipContainers) {
                if (container.equals(element) || isChildOf(element, container)) {
                    isWithinSkipContainer = true;
                    break;
                }
            }

            if (!isWithinSkipContainer) {
                // Samle tekst fra paragraphs og andre tekst-elementer
                if (tagName.matches("p|div") && !fullText.isEmpty()) {
                    String textToAdd = "";
                    boolean foundSpecialChild = false;

                    // 1. Sjekk om strong er direkte barn
                    for (Element child : element.children()) {
                        if (child.tagName().equals("strong")) {
                            textToAdd = child.text();
                            foundSpecialChild = true;
                            break;
                        }
                    }

                    if (!foundSpecialChild) {
                        for (Element child : element.children()) {
                            if (child.hasClass("note-container")) {
                                Element noteButton = child.selectFirst(".note-button, button");
                                if (noteButton != null) {
                                    textToAdd = noteButton.text();
                                    foundSpecialChild = true;
                                    break;
                                }
                            }
                        }
                    }

                    // 3. Hvis ingen spesielle barn, bruk ownText
                    if (!foundSpecialChild) {
                        textToAdd = ownText;
                    }

                    if (!textToAdd.isEmpty() && textToAdd.length() > 10) {
                        result.append(textToAdd).append("\n");
                    }
                }
            }
        }

        return result.toString();
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

    private String getAuthorInfo(final Document doc) {
        StringBuilder authorInfo = new StringBuilder();

        Element articleElement = doc.selectFirst("article");
        if (articleElement == null) {
            return "";
        }

        // Finn author/journalist elementer
        Elements authorElements = articleElement.select(
                "[class*=author], [class*=journalist], [class*=byline]");

        for (Element authorElement : authorElements) {
            String authorText = authorElement.text().trim();
            if (!authorText.isEmpty() && authorText.length() > 3) {
                // Fjern "Journalist" fra teksten hvis den finnes
                authorText = authorText.replace("– Journalist", "").replace("- Journalist", "").trim();
                if (!authorText.isEmpty()) {
                    authorInfo.append(authorText).append(", ");
                }
            }
        }

        // Fjern siste komma og legg til "Skrevet av:"
        String result = authorInfo.toString();
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2);
        }

        return result.isEmpty() ? "" : "Skrevet av: " + result;
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
