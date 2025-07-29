package folkestad.project.TextSummarizer;

import java.util.Comparator;

/**
 * Comparator for Sentence-objekter basert på setningsnummer.
 * Sorterer setninger i stigende rekkefølge etter nummer.
 */
public class SentenceComparatorForSummary implements Comparator<Sentence> {
    /**
     * Sammenligner to Sentence-objekter basert på nummer.
     *
     * @param obj1 Første Sentence-objekt
     * @param obj2 Andre Sentence-objekt
     * @return 1 hvis obj1 har høyere nummer, -1 hvis lavere, 0 hvis lik
     */
    @Override
    public int compare(Sentence obj1, Sentence obj2) {
        if (obj1.number > obj2.number) {
            return 1;
        } else if (obj1.number < obj2.number) {
            return -1;
        } else {
            return 0;
        }
    }
}

