package folkestad.project.TextSummarizer;

public class Sentence {
    /**
     * Nummeret til avsnittet denne setningen tilhører.
     */
    int paragraphNumber;
    /**
     * Setningens nummer i avsnittet.
     */
    int number;
    /**
     * Lengden på setningen (antall tegn).
     */
    int stringLength;
    /**
     * Score for setningen (brukes til summering).
     */
    double score;
    /**
     * Antall ord i setningen.
     */
    int noOfWords;
    /**
     * Selve setningsteksten.
     */
    String value;

    /**
     * Oppretter en ny Sentence med gitt nummer, tekst, lengde og avsnittsnummer.
     *
     * @param number          Setningens nummer i avsnittet
     * @param value           Setningstekst
     * @param stringLength    Lengde på setningen (antall tegn)
     * @param paragraphNumber Nummeret til avsnittet
     */
    public Sentence(int number, String value, int stringLength, int paragraphNumber) {
        this.number = number;
        this.value = value;
        this.stringLength = this.value.length();
        this.noOfWords = this.value.split("\\s+").length;
        this.score = 0.0;
        this.paragraphNumber = paragraphNumber;
    }
}
