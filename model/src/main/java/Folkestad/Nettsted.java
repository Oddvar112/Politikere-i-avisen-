package folkestad;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

/**
 * Enum som representerer forskjellige nyhetssider med deres URL-mønstre og kildeURLer.
 * Inneholder metoder for å identifisere hvilken nyhetsside en URL tilhører.
 * Støtter både RSS-feeds og frontpage DOM-scraping.
 */
@Getter
public enum Nettsted {
    
    NRK("NRK", "https://www.nrk.no/toppsaker.rss", "nrk.no", ScrapingMethod.RSS),
    VG("VG", "https://www.vg.no/", "vg.no", ScrapingMethod.FRONTPAGE_DOM),
    E24("E24", "https://e24.no/", "e24.no", ScrapingMethod.FRONTPAGE_DOM),
    AFTENPOSTEN("Aftenposten", "https://www.aftenposten.no/rss", "aftenposten.no", ScrapingMethod.RSS),
    DAGBLADET("Dagbladet", "https://www.dagbladet.no/rss", "dagbladet.no", ScrapingMethod.RSS);

    /**
     * Enum for å beskrive hvordan nettsiden scrapers.
     */
    public enum ScrapingMethod {
        RSS,           // Scraper via RSS feed
        FRONTPAGE_DOM  // Scraper via DOM på frontpage
    }

    private final String displayName;
    private final String sourceUrl;  // RSS URL eller frontpage URL
    private final String domain;
    private final ScrapingMethod scrapingMethod;

    /**
     * Konstruktør for Nettsted enum.
     *
     * @param displayName Visningsnavn for nyhetssiden
     * @param sourceUrl RSS-feed URL eller frontpage URL for nyhetssiden
     * @param domain Domene for nyhetssiden (brukes for URL-gjenkjennelse)
     * @param scrapingMethod Metode for scraping (RSS eller DOM)
     */
    Nettsted(String displayName, String sourceUrl, String domain, ScrapingMethod scrapingMethod) {
        this.displayName = displayName;
        this.sourceUrl = sourceUrl;
        this.domain = domain;
        this.scrapingMethod = scrapingMethod;
    }

    /**
     * Parser en artikkel-URL og identifiserer hvilken nyhetsside den tilhører.
     * 
     * @param url URL-en som skal analyseres
     * @return Optional med Nettsted hvis en match finnes, ellers Optional.empty()
     */
    public static Optional<Nettsted> parseFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return Optional.empty();
        }

        // Konverter til lowercase for case-insensitive sammenligning
        String lowerUrl = url.toLowerCase();

        // Søk etter domene-match i URL-en
        return Arrays.stream(Nettsted.values())
                .filter(nettsted -> lowerUrl.contains(nettsted.domain.toLowerCase()))
                .findFirst();
    }

    /**
     * Returnerer en array med alle tilgjengelige kilde-URLer (RSS eller frontpage).
     *
     * @return Array med kilde-URLer
     */
    public static String[] getAllSourceUrls() {
        return Arrays.stream(Nettsted.values())
                .map(Nettsted::getSourceUrl)
                .toArray(String[]::new);
    }

    /**
     * Returnerer en array med alle RSS-URLer (kun de som bruker RSS).
     *
     * @return Array med RSS-URLer
     */
    public static String[] getAllRssUrls() {
        return Arrays.stream(Nettsted.values())
                .filter(nettsted -> nettsted.scrapingMethod == ScrapingMethod.RSS)
                .map(Nettsted::getSourceUrl)
                .toArray(String[]::new);
    }

    /**
     * Returnerer en array med alle frontpage-URLer (kun de som bruker DOM-scraping).
     *
     * @return Array med frontpage-URLer
     */
    public static String[] getAllFrontpageUrls() {
        return Arrays.stream(Nettsted.values())
                .filter(nettsted -> nettsted.scrapingMethod == ScrapingMethod.FRONTPAGE_DOM)
                .map(Nettsted::getSourceUrl)
                .toArray(String[]::new);
    }

    /**
     * Returnerer alle nettsteder som bruker RSS.
     *
     * @return Array med Nettsted som bruker RSS
     */
    public static Nettsted[] getRssSites() {
        return Arrays.stream(Nettsted.values())
                .filter(nettsted -> nettsted.scrapingMethod == ScrapingMethod.RSS)
                .toArray(Nettsted[]::new);
    }

    /**
     * Returnerer alle nettsteder som bruker frontpage DOM-scraping.
     *
     * @return Array med Nettsted som bruker DOM-scraping
     */
    public static Nettsted[] getFrontpageSites() {
        return Arrays.stream(Nettsted.values())
                .filter(nettsted -> nettsted.scrapingMethod == ScrapingMethod.FRONTPAGE_DOM)
                .toArray(Nettsted[]::new);
    }

    /**
     * Returnerer en array med alle tilgjengelige visningsnavn.
     *
     * @return Array med visningsnavn
     */
    public static String[] getAllDisplayNames() {
        return Arrays.stream(Nettsted.values())
                .map(Nettsted::getDisplayName)
                .toArray(String[]::new);
    }

    /**
     * Finner Nettsted basert på visningsnavn.
     *
     * @param displayName Visningsnavnet som skal søkes etter
     * @return Optional med Nettsted hvis en match finnes, ellers Optional.empty()
     */
    public static Optional<Nettsted> findByDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return Optional.empty();
        }

        return Arrays.stream(Nettsted.values())
                .filter(nettsted -> nettsted.displayName.equalsIgnoreCase(displayName.trim()))
                .findFirst();
    }

    /**
     * Kontrollerer om en URL tilhører noen av de kjente nyhetssidene.
     *
     * @param url URL-en som skal kontrolleres
     * @return true hvis URL-en tilhører en kjent nyhetsside, ellers false
     */
    public static boolean isKnownNewsSource(String url) {
        return parseFromUrl(url).isPresent();
    }

    /**
     * Kontrollerer om dette nettstedet bruker RSS.
     *
     * @return true hvis nettstedet bruker RSS, false ellers
     */
    public boolean usesRss() {
        return this.scrapingMethod == ScrapingMethod.RSS;
    }

    /**
     * Kontrollerer om dette nettstedet bruker frontpage DOM-scraping.
     *
     * @return true hvis nettstedet bruker DOM-scraping, false ellers
     */
    public boolean usesFrontpageDom() {
        return this.scrapingMethod == ScrapingMethod.FRONTPAGE_DOM;
    }

    /**
     * Legacy method - returnerer sourceUrl for bakoverkompatibilitet.
     * @deprecated Bruk getSourceUrl() i stedet
     */
    @Deprecated
    public String getRssUrl() {
        return this.sourceUrl;
    }

    @Override
    public String toString() {
        return displayName + " (" + scrapingMethod + ")";
    }
}