package Folkestad.Project;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * NorwegianNameExtractor bruker CoreNLPProcessor og norsk regex for å finne og telle forekomster av norske personnavn i en tekst.
 */
public class NorwegianNameExtractor {
    private static final Pattern NAME_REGEX = Pattern.compile(
        "[A-ZÆØÅ][a-zæøå]+(?:[ \\-][A-ZÆØÅ][a-zæøå]+){1,2}"
    );
    private final CoreNLPProcessor nlpProcessor;

    /**
     * Konstruktør som oppretter NorwegianNameExtractor med CoreNLP processor.
     */
    public NorwegianNameExtractor() {
        this.nlpProcessor = new CoreNLPProcessor();
    }

    /**
     * Ekstraherer og returnerer alle navn fra en tekst, med all logikk for merging og filtrering.
     * Kjør norsk regex på hele teksten, så kjør NLP på alle regex-funnede navn.
     */
    public Set<String> extractNames(String text) {
        Set<String> regexNames = new HashSet<>(extractNamesWithRegex(text));
        Set<String> finalNames = new HashSet<>();
        
        for (String candidate : regexNames) {
            List<String> nlpNames = nlpProcessor.extractPersonNames(candidate);
            finalNames.addAll(nlpNames);
        }
        return finalNames;
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
     * Sjekker om navnet matcher norsk navneregex.
     */
    private boolean isValidNorwegianName(String name) {
        Matcher matcher = NAME_REGEX.matcher(name);
        return matcher.matches();
    }

    /**
     * Lukker CoreNLP processor og frigjør ressurser.
     * Bør kalles når NorwegianNameExtractor ikke lenger skal brukes.
     */
    public void close() {
        if (nlpProcessor != null) {
            nlpProcessor.close();
        }
    }

    public static void main(String[] args) {
        String text = "Ola Nordmann og Kari Nordmann gikk til Oslo sammen med Per Arne Hansen. I parken møtte de Anne-Marie Johansen, Lars Ove Nilsen og Siri. Senere kom også Pål Ødegård, Åse-Berit Olsen og Knut. På kafeen satt Eva-Lill Andersen, Jon Olav Ryen, og en venn som het Magnus. I avisen sto det om Henrik Ibsen, men også om Ola Nordmann. Noen ropte: «Hei, Kari Nordmann!» og «Kom hit, Per Arne!». Til slutt kom også Sigrid Undset, Bjørn Eidsvåg, og en ukjent person som bare ble kalt «Mann». ";
        NorwegianNameExtractor extractor = new NorwegianNameExtractor();
        System.out.println(extractor.extractNames(text));
    }
}
