package folkestad.project.TextSummarizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Text summarizer som følger nøyaktig samme logikk som original SummaryTool.
 * Konvertert til å ta tekst som input i stedet for å lese fra fil.
 */
public class TextSummarizer {

    private ArrayList<Sentence> sentences;
    private ArrayList<Paragraph> paragraphs;
    private ArrayList<Sentence> contentSummary;
    private int noOfSentences;
    private int noOfParagraphs;
    private double[][] intersectionMatrix;
    private LinkedHashMap<Sentence, Double> dictionary;

    public TextSummarizer() {
        init();
    }

    private void init() {
        sentences = new ArrayList<>();
        paragraphs = new ArrayList<>();
        contentSummary = new ArrayList<>();
        dictionary = new LinkedHashMap<>();
        noOfSentences = 0;
        noOfParagraphs = 0;
    }

    /**
     * Hovedmetode for å generere sammendrag fra tekst - følger original SummaryTool flyt
     */
    public SummaryResult summarize(String inputText) {
        if (inputText == null || inputText.trim().isEmpty()) {
            return new SummaryResult("", 0, 0, 0.0);
        }

        // Reset state
        init();

        try {
            // Følger nøyaktig samme steg som original:
            extractSentenceFromContext(inputText);
            
            if (sentences.isEmpty()) {
                return new SummaryResult("Ingen setninger funnet i teksten.", 0, 0, 0.0);
            }

            groupSentencesIntoParagraphs();
            createIntersectionMatrix();
            createDictionary();
            createSummary();

            return buildSummaryResult();

        } catch (Exception e) {
            return new SummaryResult("Feil under generering av sammendrag: " + e.getMessage(), 0, 0, 0.0);
        }
    }

    /**
     * Ekstraherer setninger fra tekst - tilsvarende original extractSentenceFromContext()
     * Men tar tekst som parameter i stedet for å lese fra fil
     */
    private void extractSentenceFromContext(String inputText) {
        String[] lines = inputText.split("\n");
        int prevChar = -1;
        
        StringBuilder currentSentence = new StringBuilder();
        
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                char nextChar = line.charAt(i);
                
                if (nextChar != '.') {
                    currentSentence.append(nextChar);
                } else {
                    // Funnet slutt på setning
                    String sentenceText = currentSentence.toString().trim();
                    if (sentenceText.length() > 0) {
                        sentences.add(new Sentence(noOfSentences, sentenceText, sentenceText.length(), noOfParagraphs));
                        noOfSentences++;
                    }
                    currentSentence.setLength(0);
                }
                
                // Sjekk for ny paragraf (to linefeed etter hverandre)
                if (nextChar == '\n' && prevChar == '\n') {
                    noOfParagraphs++;
                }
                
                prevChar = nextChar;
            }
            
            // Legg til linefeed på slutten av hver linje
            if (currentSentence.length() > 0) {
                currentSentence.append('\n');
            }
            prevChar = '\n';
        }
        
        // Legg til siste setning hvis den ikke ender med punktum
        String lastSentence = currentSentence.toString().trim();
        if (lastSentence.length() > 0) {
            sentences.add(new Sentence(noOfSentences, lastSentence, lastSentence.length(), noOfParagraphs));
            noOfSentences++;
        }
    }

    /**
     * Nøyaktig samme logikk som original groupSentencesIntoParagraphs()
     */
    private void groupSentencesIntoParagraphs() {
        int paraNum = 0;
        Paragraph paragraph = new Paragraph(0);

        for (int i = 0; i < noOfSentences; i++) {
            if (sentences.get(i).paragraphNumber == paraNum) {
                // continue
            } else {
                paragraphs.add(paragraph);
                paraNum++;
                paragraph = new Paragraph(paraNum);
            }
            paragraph.sentences.add(sentences.get(i));
        }

        paragraphs.add(paragraph);
    }

    /**
     * Nøyaktig samme logikk som original noOfCommonWords()
     */
    private double noOfCommonWords(Sentence str1, Sentence str2) {
        double commonCount = 0;

        for (String str1Word : str1.value.split("\\s+")) {
            for (String str2Word : str2.value.split("\\s+")) {
                if (str1Word.compareToIgnoreCase(str2Word) == 0) {
                    commonCount++;
                }
            }
        }

        return commonCount;
    }

    /**
     * Nøyaktig samme logikk som original createIntersectionMatrix()
     */
    private void createIntersectionMatrix() {
        intersectionMatrix = new double[noOfSentences][noOfSentences];
        for (int i = 0; i < noOfSentences; i++) {
            for (int j = 0; j < noOfSentences; j++) {

                if (i <= j) {
                    Sentence str1 = sentences.get(i);
                    Sentence str2 = sentences.get(j);
                    intersectionMatrix[i][j] = noOfCommonWords(str1, str2) / ((double)(str1.noOfWords + str2.noOfWords) / 2);
                } else {
                    intersectionMatrix[i][j] = intersectionMatrix[j][i];
                }
            }
        }
    }

    /**
     * Nøyaktig samme logikk som original createDictionary()
     */
    private void createDictionary() {
        for (int i = 0; i < noOfSentences; i++) {
            double score = 0;
            for (int j = 0; j < noOfSentences; j++) {
                score += intersectionMatrix[i][j];
            }
            dictionary.put(sentences.get(i), score);
            sentences.get(i).score = score;
        }
    }

    /**
     * Nøyaktig samme logikk som original createSummary()
     * Velger 1 setning per 5 setninger i hver paragraf (20% som original)
     */
    private void createSummary() {
        for (int j = 0; j <= noOfParagraphs && j < paragraphs.size(); j++) {
            int primary_set = paragraphs.get(j).sentences.size() / 5; // SAMME ratio som original

            // Sort based on score (importance)
            Collections.sort(paragraphs.get(j).sentences, new SentenceComparator());
            
            for (int i = 0; i <= primary_set && i < paragraphs.get(j).sentences.size(); i++) {
                contentSummary.add(paragraphs.get(j).sentences.get(i));
            }
        }

        // To ensure proper ordering - samme som original
        Collections.sort(contentSummary, new SentenceComparatorForSummary());
    }

    /**
     * Bygger resultat-streng fra contentSummary
     */
    private SummaryResult buildSummaryResult() {
        StringBuilder summary = new StringBuilder();
        
        for (Sentence sentence : contentSummary) {
            summary.append(sentence.value.trim());
            if (!sentence.value.trim().endsWith(".") && 
                !sentence.value.trim().endsWith("!") && 
                !sentence.value.trim().endsWith("?")) {
                summary.append(".");
            }
            summary.append(" ");
        }

        int originalWordCount = getWordCount(sentences);
        int summaryWordCount = getWordCount(contentSummary);
        double compressionRatio = originalWordCount > 0 ? (double) summaryWordCount / originalWordCount : 0.0;

        return new SummaryResult(
            summary.toString().trim(),
            originalWordCount,
            summaryWordCount,
            compressionRatio
        );
    }

    /**
     * Samme som original getWordCount()
     */
    private int getWordCount(ArrayList<Sentence> sentenceList) {
        int wordCount = 0;
        for (Sentence sentence : sentenceList) {
            wordCount += sentence.noOfWords;
        }
        return wordCount;
    }

}