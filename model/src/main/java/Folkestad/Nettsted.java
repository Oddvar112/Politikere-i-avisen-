package folkestad;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

/**
 * Enum som representerer forskjellige nyhetssider med deres URL-mønstre og RSS-feeder.
 * Inneholder metoder for å identifisere hvilken nyhetsside en URL tilhører.
 */
@Getter
public enum Nettsted {
    
    NRK("NRK", "https://www.nrk.no/toppsaker.rss", "nrk.no"),
    VG("VG", "https://www.vg.no/rss/feed/", "vg.no"),
    E24("E24", "https://e24.no/rss", "e24.no"),
    AFTENPOSTEN("Aftenposten", "https://www.aftenposten.no/rss", "aftenposten.no"),
    DAGBLADET("Dagbladet", "https://www.dagbladet.no/rss", "dagbladet.no");

    private final String displayName;
    private final String rssUrl;
    private final String domain;

    /**
     * Konstruktør for Nettsted enum.
     *
     * @param displayName Visningsnavn for nyhetssiden
     * @param rssUrl RSS-feed URL for nyhetssiden
     * @param domain Domene for nyhetssiden (brukes for URL-gjenkjennelse)
     */
    Nettsted(String displayName, String rssUrl, String domain) {
        this.displayName = displayName;
        this.rssUrl = rssUrl;
        this.domain = domain;
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
     * Returnerer en array med alle tilgjengelige RSS-URLer.
     *
     * @return Array med RSS-URLer
     */
    public static String[] getAllRssUrls() {
        return Arrays.stream(Nettsted.values())
                .map(Nettsted::getRssUrl)
                .toArray(String[]::new);
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

    @Override
    public String toString() {
        return displayName;
    }
}
