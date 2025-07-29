package folkestad.project.TextSummarizer;

import java.util.Comparator;

/**
 * Comparator for Sentence-objekter basert på score.
 * Sorterer setninger med høyest score først.
 */
public class SentenceComparator implements Comparator<Sentence> {
    /**
     * Sammenligner to Sentence-objekter basert på score.
     *
     * @param obj1 Første Sentence-objekt
     * @param obj2 Andre Sentence-objekt
     * @return -1 hvis obj1 har høyere score, 1 hvis lavere, 0 hvis lik
     */
    @Override
    public int compare(Sentence obj1, Sentence obj2) {
        if (obj1.score > obj2.score) {
            return -1;
        } else if (obj1.score < obj2.score) {
            return 1;
        } else {
            return 0;
        }
    }
}

