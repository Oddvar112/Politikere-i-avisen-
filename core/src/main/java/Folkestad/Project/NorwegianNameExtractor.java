package Folkestad.Project;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * NorwegianNameExtractor bruker Stanford CoreNLP og norsk regex for å finne og telle forekomster av norske personnavn i en tekst.
 */
public class NorwegianNameExtractor {
    private static final Pattern NAME_REGEX = Pattern.compile(
        "[A-ZÆØÅ][a-zæøå]+[ \\-][A-ZÆØÅ][a-zæøå]+"
    );
    private static final String PIPELINE_COMPONENTS = "tokenize, ssplit, pos, lemma, ner";

    /**
     * Ekstraherer og returnerer alle navn fra en tekst, med all logikk for merging og filtrering.
     * Kjør norsk regex på hele teksten, så kjør NLP på alle regex-funnede navn.
     */
    public Set<String> extractNames(String text) {
        Set<String> regexNames = new HashSet<>(extractNamesWithRegex(text));
        Set<String> finalNames = new HashSet<>();
        StanfordCoreNLP pipeline = createPipeline();
        for (String candidate : regexNames) {
            for (String nlpName : extractCandidateNamesFromText(candidate, pipeline)) {
                finalNames.add(nlpName);
            }
        }
        return (finalNames);
    }

    /**
     * Bruker norsk regex for å hente ut navn direkte fra tekst, og filtrerer med isValidNorwegianName.
     */
    private List<String> extractNamesWithRegex(String text) {
        List<String> names = new ArrayList<>();
        Matcher matcher = NAME_REGEX.matcher(text);
        while (matcher.find()) {
            String name = matcher.group();
            if (isValidNorwegianName(name)) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * Kjører CoreNLP pipeline og henter ut kandidatnavn fra tokens for en tekstbit.
     */
    private List<String> extractCandidateNamesFromText(String text, StanfordCoreNLP pipeline) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        List<String> names = new ArrayList<>();
        StringBuilder currentName = new StringBuilder();
        for (CoreLabel token : document.tokens()) {
            String entity = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            String word = token.originalText();
            if ("PERSON".equals(entity)) {
                if (currentName.length() > 0) currentName.append(" ");
                currentName.append(word);
            } else {
                if (currentName.length() > 0) {
                    names.add(currentName.toString());
                    currentName.setLength(0);
                }
            }
        }
        if (currentName.length() > 0) {
            names.add(currentName.toString());
        }
        return names;
    }

    /**
     * Oppretter og konfigurerer StanfordCoreNLP pipeline.
     */
    private StanfordCoreNLP createPipeline() {
        Properties props = new Properties();
        props.setProperty("annotators", PIPELINE_COMPONENTS);
        props.setProperty("coref.algorithm", "neural");
        return new StanfordCoreNLP(props);
    }

    /**
     * Sjekker om navnet matcher norsk navneregex.
     */
    private boolean isValidNorwegianName(String name) {
        Matcher matcher = NAME_REGEX.matcher(name);
        return matcher.matches();
    }

    public static void main(String[] args) {
        String text = "Ola Nordmann og Kari Nordmann gikk til Oslo sammen med Per Arne Hansen. I parken møtte de Anne-Marie Johansen, Lars Ove Nilsen og Siri. Senere kom også Pål Ødegård, Åse-Berit Olsen og Knut. På kafeen satt Eva-Lill Andersen, Jon Olav Ryen, og en venn som het Magnus. I avisen sto det om Henrik Ibsen, men også om Ola Nordmann. Noen ropte: «Hei, Kari Nordmann!» og «Kom hit, Per Arne!». Til slutt kom også Sigrid Undset, Bjørn Eidsvåg, og en ukjent person som bare ble kalt «Mann». ";
        NorwegianNameExtractor extractor = new NorwegianNameExtractor();
        System.out.println(extractor.extractNames(text));
    }
}
