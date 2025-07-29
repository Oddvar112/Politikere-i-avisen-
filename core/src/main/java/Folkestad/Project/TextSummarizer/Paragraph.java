package folkestad.project.TextSummarizer;

import java.util.ArrayList;

/**
 * Representerer et avsnitt i en tekst, med avsnittsnummer og tilhørende
 * setninger.
 */
public class Paragraph {
    /**
     * Avsnittsnummer.
     */
    int number;
    /**
     * Liste med setninger i avsnittet.
     */
    ArrayList<Sentence> sentences;

    /**
     * Oppretter et nytt Paragraph-objekt med gitt nummer og tom setningsliste.
     *
     * @param number Avsnittsnummer
     */
    public Paragraph(int number) {
        this.number = number;
        this.sentences = new ArrayList<>();
    }
}

