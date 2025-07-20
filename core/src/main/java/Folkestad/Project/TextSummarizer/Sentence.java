package folkestad.project.TextSummarizer;

public class Sentence {
    int paragraphNumber;
    int number;
    int stringLength;
    double score;
    int noOfWords;
    String value;

    public Sentence(int number, String value, int stringLength, int paragraphNumber) {
        this.number = number;
        this.value = value;
        this.stringLength = this.value.length();
        this.noOfWords = this.value.split("\\s+").length;
        this.score = 0.0;
        this.paragraphNumber = paragraphNumber;
    }
}
