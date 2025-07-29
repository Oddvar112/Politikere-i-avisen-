package folkestad.project.extractors;

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

    /**
     * Laster kandidatnavn - mye enklere nå med unike navn som primærnøkler!
     */
    private void loadKandidatNames() {
        if (kandidatNamesMap == null) {
            List<KandidatStortingsvalg> allKandidater = kandidatRepository.findAll();

            kandidatNamesMap = new HashMap<>();

            for (KandidatStortingsvalg kandidat : allKandidater) {
                if (kandidat.getNavn() != null && !kandidat.getNavn().trim().isEmpty()) {
                    String originalName = kandidat.getNavn().trim();
                    String lowerCaseName = originalName.toLowerCase();

                    // Ingen duplikathåndtering lenger nødvendig - hvert navn er unikt!
                    kandidatNamesMap.put(lowerCaseName, originalName);
                }
            }
        }
    }

    /**
     * Ekstraherer kandidatnavn fra tekst ved hjelp av regex og matcher mot
     * databasen.
     * Mye enklere nå uten duplikathåndtering!
     *
     * @param text teksten som skal analyseres for kandidatnavn
     * @return sett med ekstraherte kandidatnavn som finnes i databasen
     */
    @Override
    public Set<String> extractNames(final String text) {
        loadKandidatNames();

        if (kandidatNamesMap == null || kandidatNamesMap.isEmpty()) {
            return new HashSet<>();
        }

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

