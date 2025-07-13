package folkestad.project.extractors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import folkestad.project.CoreNLPProcessor;

import java.util.regex.Matcher;

/**
 * NorwegianNameExtractor bruker CoreNLPProcessor og norsk regex for å finne og telle forekomster av norske personnavn i en tekst.
 */
public class NorwegianNameExtractor {
    private static final Pattern NAME_REGEX = Pattern.compile(
        "[A-ZÆØÅ][a-zæøå]+(?:[ \\-][A-ZÆØÅ][a-zæøå]+){1,2}"
    );
    private CoreNLPProcessor nlpProcessor;

    /**
     * Konstruktør som oppretter NorwegianNameExtractor uten å initialisere CoreNLP med en gang.
     * CoreNLP blir initialisert lazy når det trengs.
     */
    public NorwegianNameExtractor() {
    }

    private CoreNLPProcessor getNlpProcessor() {
        if (nlpProcessor == null) {
            nlpProcessor = new CoreNLPProcessor();
        }
        return nlpProcessor;
    }

    /**
     * Ekstraherer og returnerer alle navn fra en tekst, med all logikk for merging og filtrering.
     * Kjør norsk regex på hele teksten, så kjør NLP på alle regex-funnede navn.
     *
     * @param text teksten som skal analyseres for navn
     * @return sett med ekstraherte navn
     */
    public Set<String> extractNames(final String text) {
        Set<String> regexNames = new HashSet<>(extractNamesWithRegex(text));
        Set<String> finalNames = new HashSet<>();

        for (String candidate : regexNames) {
            List<String> nlpNames = getNlpProcessor().extractPersonNames(candidate);
            finalNames.addAll(nlpNames);
        }
        return finalNames;
    }

    /**
     * Bruker norsk regex for å hente ut navn direkte fra tekst, og filtrerer med isValidNorwegianName.
     *
     * @param text teksten som skal analyseres
     * @return liste med navn funnet av regex
     */
    public List<String> extractNamesWithRegex(final String text) {
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
     *
     * @param name navnet som skal valideres
     * @return true hvis navnet er gyldig, false ellers
     */
    private boolean isValidNorwegianName(final String name) {
        Matcher matcher = NAME_REGEX.matcher(name);
        return matcher.matches();
    }
}

