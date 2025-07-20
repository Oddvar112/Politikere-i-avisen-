package folkestad.project.scrapers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import folkestad.project.TextSummarizer.TextSummarizer;
import folkestad.project.TextSummarizer.SummaryResult;

public class NRKBodyTextTest {
    public static void main(String[] args) {
        String url = "https://www.nrk.no/norge/ingvar-ambjornsen-er-dod_-__varm_-sindig_og-alltid-apen-1.17498464";
        try {
            Document doc = Jsoup.connect(url).get();
            NRKScraper scraper = new NRKScraper(url);
            String bodyText = scraper.getAllText(doc);
            TextSummarizer summarizer = new TextSummarizer();
            SummaryResult result = summarizer.summarize(bodyText);
            System.out.println("==================== ORIGINAL TEKST ====================\n");
            System.out.println(bodyText);
            System.out.println("\n==================== SAMMENDRAG ====================\n");
            System.out.println(result.getSummary());
            System.out.println("\n==================== STATISTIKK ====================");
            System.out.printf("Antall ord original: %d\n", result.getOriginalWordCount());
            System.out.printf("Antall ord sammendrag: %d\n", result.getSummaryWordCount());
            System.out.printf("Kompresjonsrate: %.2f\n", result.getCompressionRatio());
        } catch (IOException e) {
            System.err.println("Feil ved henting av artikkel: " + e.getMessage());
        }
    }
}
