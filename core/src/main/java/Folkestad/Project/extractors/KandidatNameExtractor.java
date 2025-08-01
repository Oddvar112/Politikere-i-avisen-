package folkestad.project.extractors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import folkestad.KandidatStortingsvalg;
import folkestad.KandidatStortingsvalgRepository;

@Component
public class KandidatNameExtractor extends NorwegianNameExtractor {

    @Autowired
    private KandidatStortingsvalgRepository kandidatRepository;

    private Map<String, String> kandidatNamesMap = null;
    
    /**
     * Kjente alias som kan matches direkte til kandidater.
     */
    private static final String[] KJENTE_ALIAS = {
        "jonas gahr støre", "støre",
        "sylvi listhaug", "listhaug",
        "trygve slagsvold vedum", "vedum",
        "bjørnar moxnes", "moxnes",
        "sandra borch", "borch",
        "peter christian frølich", "peter frølich", "frølich"  // <- Legg til "frølich" også
    };

    public KandidatNameExtractor() {
        super();
    }

    /**
     * Laster kandidatnavn fra databasen.
     */
    private void loadKandidatNames() {
        if (kandidatNamesMap == null) {
            List<KandidatStortingsvalg> allKandidater = kandidatRepository.findAll();
            kandidatNamesMap = new HashMap<>();

            for (KandidatStortingsvalg kandidat : allKandidater) {
                if (kandidat.getNavn() != null && !kandidat.getNavn().trim().isEmpty()) {
                    String originalName = kandidat.getNavn().trim();
                    String lowerCaseName = originalName.toLowerCase();
                    kandidatNamesMap.put(lowerCaseName, originalName);
                }
            }
        }
    }

    /**
     * Søker etter kjente alias i teksten og returnerer fullstendige navn.
     * 
     * @param text Teksten som skal analyseres
     * @return Set med fullstendige navn basert på funnet alias
     */
    private Set<String> extractKnownAlias(final String text) {
        Set<String> foundNames = new HashSet<>();
        
        for (String alias : KJENTE_ALIAS) {
            // Sjekk om alias finnes i teksten (case-insensitive, hele ord)
            String regex = "\\b" + Pattern.quote(alias) + "\\b";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            
            if (matcher.find()) {
                // Finn tilsvarende kandidat i databasen
                String matchingKandidat = findMatchingKandidat(alias);
                if (matchingKandidat != null) {
                    foundNames.add(matchingKandidat);
                }
            }
        }
        
        return foundNames;
    }

    /**
     * Finner kandidat i databasen som matcher et gitt alias.
     * 
     * @param alias Alias som skal matches
     * @return Fullstendig kandidatnavn fra databasen, eller null hvis ikke funnet
     */
    private String findMatchingKandidat(final String alias) {
        String lowerAlias = alias.toLowerCase();
        
        // Først: Prøv eksakt match
        if (kandidatNamesMap.containsKey(lowerAlias)) {
            return kandidatNamesMap.get(lowerAlias);
        }
        
        // Deretter: Søk gjennom alle kandidater og finn beste match
        for (Map.Entry<String, String> entry : kandidatNamesMap.entrySet()) {
            String kandidatNavnLower = entry.getKey();
            String kandidatNavnOriginal = entry.getValue();
            
            // Sjekk om kandidatens navn inneholder alle ord fra alias
            if (containsAllWords(kandidatNavnLower, lowerAlias)) {
                return kandidatNavnOriginal;
            }
        }
        
        return null;
    }

    /**
     * Sjekker om en tekst inneholder alle ord fra en annen tekst.
     * 
     * @param text Teksten som skal søkes i
     * @param searchWords Ordene som skal finnes
     * @return true hvis alle ord finnes
     */
    private boolean containsAllWords(final String text, final String searchWords) {
        String[] words = searchWords.split("\\s+");
        
        for (String word : words) {
            if (!text.contains(word.trim())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Ekstraherer kandidatnavn fra tekst ved hjelp av regex, database-matching 
     * og kjente alias.
     * 
     * @param text teksten som skal analyseres for kandidatnavn
     * @return sett med ekstraherte kandidatnavn som finnes i databasen
     */
    @Override
    public Set<String> extractNames(final String text) {
        loadKandidatNames();

        Set<String> allFoundNames = new HashSet<>();

        if (kandidatNamesMap != null && !kandidatNamesMap.isEmpty()) {
            List<String> regexNames = super.extractNamesWithRegex(text);
            
            for (String regexName : regexNames) {
                String normalizedName = regexName.toLowerCase();
                String originalName = kandidatNamesMap.get(normalizedName);

                if (originalName != null) {
                    allFoundNames.add(originalName);
                }
            }
        }

        Set<String> aliasMatches = extractKnownAlias(text);
        allFoundNames.addAll(aliasMatches);

        return allFoundNames;
    }
}