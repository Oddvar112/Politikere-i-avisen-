package folkestad.project.TextSummarizer;

import java.util.ArrayList;

public class Paragraph {
    int number;
    ArrayList<Sentence> sentences;

    public Paragraph(int number) {
        this.number = number;
        this.sentences = new ArrayList<>();
    }
}
