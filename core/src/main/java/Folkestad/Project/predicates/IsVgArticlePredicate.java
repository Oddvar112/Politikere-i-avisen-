package folkestad.project.predicates;

import java.util.function.Predicate;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Predicate for determining if a given Jsoup Document represents a genuine VG
 * article.
 * <p>
 * All VG articles have an author. If the document is not an article (e.g.,
 * front page),
 * the author will be "VG" or missing.
 * </p>
 */
public class IsVgArticlePredicate implements Predicate<Document> {

    /**
     * Tests whether the provided Jsoup Document is a VG article.
     *
     * @param doc the Jsoup Document to test
     * @return true if the document is an article, false otherwise
     */
    @Override
    public boolean test(final Document doc) {
        Elements authorMeta = doc.select("meta[property=article:author]");
        if (!authorMeta.isEmpty()) {
            String authorContent = authorMeta.attr("content");
            if (authorContent != null && !authorContent.trim().isEmpty() &&
                    !authorContent.equalsIgnoreCase("vg") &&
                    !authorContent.equalsIgnoreCase("vg.no")) {
                return true;
            }
        }
        Elements authorMetaName = doc.select("meta[name=author]");
        if (!authorMetaName.isEmpty()) {
            String authorContent = authorMetaName.attr("content");
            if (authorContent != null && !authorContent.trim().isEmpty() &&
                    !authorContent.equalsIgnoreCase("vg") &&
                    !authorContent.equalsIgnoreCase("vg.no")) {
                return true;
            }
        }
        return false;
    }
}
