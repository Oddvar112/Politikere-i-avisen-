package folkestad;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

/**
 * Enum som representerar olika nyhetssidor med deras URL-patterns och RSS-feeds.
 * Innehåller metoder för att identifiera vilken nyhetssida en URL tillhör.
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
     * Konstruktor för Nettsted enum.
     *
     * @param displayName Visningsnamn för nyhetssidan
     * @param rssUrl RSS-feed URL för nyhetssidan
     * @param domain Domän för nyhetssidan (används för URL-igenkänning)
     */
    Nettsted(String displayName, String rssUrl, String domain) {
        this.displayName = displayName;
        this.rssUrl = rssUrl;
        this.domain = domain;
    }

    /**
     * Parsar en artikel-URL och identifierar vilken nyhetssida den tillhör.
     * 
     * @param url URL:en som ska analyseras
     * @return Optional med Nettsted om en matchning hittas, annars Optional.empty()
     */
    public static Optional<Nettsted> parseFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return Optional.empty();
        }

        // Konvertera till lowercase för case-insensitive jämförelse
        String lowerUrl = url.toLowerCase();

        // Leta efter domän-matchning i URL:en
        return Arrays.stream(Nettsted.values())
                .filter(nettsted -> lowerUrl.contains(nettsted.domain.toLowerCase()))
                .findFirst();
    }

    /**
     * Returnerar en array med alla tillgängliga RSS-URLs.
     *
     * @return Array med RSS-URLs
     */
    public static String[] getAllRssUrls() {
        return Arrays.stream(Nettsted.values())
                .map(Nettsted::getRssUrl)
                .toArray(String[]::new);
    }

    /**
     * Returnerar en array med alla tillgängliga visningsnamn.
     *
     * @return Array med visningsnamn
     */
    public static String[] getAllDisplayNames() {
        return Arrays.stream(Nettsted.values())
                .map(Nettsted::getDisplayName)
                .toArray(String[]::new);
    }

    /**
     * Hittar Nettsted baserat på visningsnamn.
     *
     * @param displayName Visningsnamnet att leta efter
     * @return Optional med Nettsted om en matchning hittas, annars Optional.empty()
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
     * Kontrollerar om en URL tillhör någon av de kända nyhetssidorna.
     *
     * @param url URL:en som ska kontrolleras
     * @return true om URL:en tillhör en känd nyhetssida, annars false
     */
    public static boolean isKnownNewsSource(String url) {
        return parseFromUrl(url).isPresent();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
