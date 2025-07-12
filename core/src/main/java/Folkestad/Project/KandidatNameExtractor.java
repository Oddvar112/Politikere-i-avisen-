package folkestad.project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import folkestad.KandidatStortingsvalg;
import folkestad.KandidatStortingsvalgRepository;

@Component
public class KandidatNameExtractor extends NorwegianNameExtractor {

    @Autowired
    private KandidatStortingsvalgRepository kandidatRepository;
    
    private Map<String, String> kandidatNamesMap = null;

    public KandidatNameExtractor() {
        super();
    }

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
     * Override extractNames for å bare bruke regex uten CoreNLP
     * @param text teksten som skal analyseres for kandidatnavn
     * @return sett med ekstraherte kandidatnavn som finnes i databasen
     */
    @Override
    public Set<String> extractNames(final String text) {
        loadKandidatNames();
        List<String> regexNames = super.extractNamesWithRegex(text);
        Set<String> matchedKandidater = new HashSet<>();
        for (String regexName : regexNames) {
            String normalizedName = regexName.toLowerCase();
            String originalName = kandidatNamesMap.get(normalizedName);
            if (originalName != null) {
                matchedKandidater.add(originalName);
            }
        }
        return matchedKandidater;
    }
}