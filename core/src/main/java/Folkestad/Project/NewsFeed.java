package folkestad.project;

import java.util.Arrays;

/**
 * Enum for norske nyhetskilder og deres RSS-feeder.
 */
public enum NewsFeed {
    VG_FORSIDEN("VG Forsiden", "https://www.vg.no/rss/feed/forsiden/"),
    VG_INNENRIKS("VG Innenriks", "https://www.vg.no/rss/feed/?categories=1069"),
    VG_UTENRIKS("VG Utenriks", "https://www.vg.no/rss/feed/?categories=1070"),
    E24_ALLE_NYHETER("E24 – Alle nyheter", "http://e24.no/rss2/"),
    E24_BORS_OG_FINANS("E24 – Børs og finans", "https://e24.no/rss2/?seksjon=boers-og-finans"),
    E24_AKSJETIPS("E24 – Aksjetips", "http://e24.no/rss2/?seksjon=aksjetips"),
    E24_IT_TELEKOM("E24 – IT & Telekom", "http://e24.no/rss2/?seksjon=it"),
    NRK_FEEDS_OVERSIKT("NRK – Oversikt over RSS feeds", "https://www.nrk.no/rss/"),
    NRK_TOPPSAKER("NRK – Toppsaker", "https://www.nrk.no/toppsaker.rss"),
    NRK_SISTE_NYHETER("NRK – Siste nyheter", "https://www.nrk.no/nyheter/siste.rss"),
    NRK_INNENRIKS("NRK – Innenriks", "https://www.nrk.no/norge/toppsaker.rss");

    private final String displayName;
    private final String url;

    NewsFeed(final String displayName, final String url) {
        this.displayName = displayName;
        this.url = url;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Returnerer alle NewsFeed-verdier for en gitt kilde (f.eks. "vg", "nrk", "e24", "tv2").
     * Kildenavn er case-insensitive og matcher dynamisk på enum-navn.
     *
     * @param source kildenavn som skal søkes etter
     * @return array med NewsFeed-verdier som matcher kilden
     */
    public static NewsFeed[] getFeedsBySource(final String source) {
        if (source == null) {
            return new NewsFeed[0];
        }
        String src = source.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(feed -> feed.name().toLowerCase().startsWith(src + "_"))
                .toArray(NewsFeed[]::new);
    }
}
