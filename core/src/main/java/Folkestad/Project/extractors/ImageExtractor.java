package folkestad.project.extractors;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class ImageExtractor {
    /**
     * Henter første bilde med gyldig HTTPS URL fra artikkelen
     * @param doc Jsoup Document for artikkelen
     * @return HTTPS URL til første bilde, eller null hvis ingen funnet
     */
    public String extractFirstImage(Document doc) {
        Elements ogImage = doc.select("meta[property=og:image]");
        if (!ogImage.isEmpty()) {
            String imageUrl = ogImage.attr("content");
            if (isValidHttpsImageUrl(imageUrl)) {
                return imageUrl;
            }
        }

        return null; 
    }


    /**
     * Sjekker om URL er gyldig HTTPS bilde-URL
     */
    private boolean isValidHttpsImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        if (!url.startsWith("https://")) {
            return false;
        }
        String lowerUrl = url.toLowerCase();
        if (!lowerUrl.matches(".*\\.(jpg|jpeg|png|webp|gif|svg)(\\?.*)?$")) {
            return false;
        }
        if (url.length() < 20) {
            return false;
        }
        return true;
    }
}
