package folkestad.project.analysis;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import folkestad.project.ArtikelDTO;
import folkestad.project.Person;
import folkestad.project.dataDTO;

/**
 * Analyserer og filtrerer kandidatdata basert på kilde
 */
public class KildeDataAnalyzer {

    /**
     * Filtrerer rådata basert på kilde og returnerer dataDTO
     * 
     * @param rawData Liste med Object[] fra database
     * @param kilde   Kilde å filtrere på ("vg.no", "nrk.no", "e24.no", "ALT" for
     *                alle)
     * @return dataDTO med filtrerte data
     */
    public static dataDTO analyzeDataByKilde(List<Object[]> rawData, String kilde) {

        if (rawData == null || rawData.isEmpty()) {
            return new dataDTO(0.0, 0, new ArrayList<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                    new HashMap<>(), kilde);
        }

        List<Person> allePersoner = new ArrayList<>();
        List<Integer> aldersListe = new ArrayList<>();
        Map<String, Integer> kjoennRatio = new HashMap<>();
        Map<String, Integer> partiMentions = new HashMap<>();
        int totaltAntallArtikler = 0;

        for (Object[] rad : rawData) {
            String navn = (String) rad[0];
            String normalizedParti = PartiNameNormalizer.normalizePartiName((String) rad[1]);
            Integer alder = (Integer) rad[2];
            String kjoenn = (String) rad[3];
            String valgdistrikt = (String) rad[4];
            String lenkerString = (String) rad[5];
            String scrapedAtString = (String) rad[6];

            if (lenkerString != null && !lenkerString.isEmpty()) {
                String[] alleLenker = lenkerString.split(",");
                String[] alleScrapedAt = (scrapedAtString != null && !scrapedAtString.isEmpty())
                        ? scrapedAtString.split(",")
                        : new String[0];

                List<ArtikelDTO> artikler = new ArrayList<>();
                for (int i = 0; i < alleLenker.length; i++) {
                    String lenke = alleLenker[i].trim();
                    if (kilde.equals("ALT") || lenke.contains(kilde)) {
                        ArtikelDTO artikkel = new ArtikelDTO();
                        artikkel.setLenke(lenke);
                        if (i < alleScrapedAt.length) {
                            String scrapedAtStr = alleScrapedAt[i].trim();
                            artikkel.setScraped(LocalDate.parse(scrapedAtStr.substring(0, 10)));
                        }
                        artikler.add(artikkel);
                    }
                }

                if (!artikler.isEmpty()) {
                    Person person = new Person();
                    person.setNavn(navn);
                    person.setParti(normalizedParti);
                    person.setAlder(alder);
                    person.setKjoenn(kjoenn);
                    person.setValgdistrikt(valgdistrikt);
                    person.setAntallArtikler(artikler.size());
                    person.setLenker(artikler);

                    allePersoner.add(person);

                    partiMentions.merge(normalizedParti, artikler.size(), Integer::sum);

                    if (alder != null) {
                        aldersListe.add(alder);
                    }

                    if (kjoenn != null && !kjoenn.isEmpty()) {
                        kjoennRatio.merge(kjoenn, 1, Integer::sum);
                    }

                    totaltAntallArtikler += artikler.size();
                }
            }
        }

        double gjennomsnittligAlder = aldersListe.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        Map<String, Double> partiProsentFordeling = beregnPartiProsent(partiMentions);
        Map<String, Double> kjoennProsentFordeling = beregnKjoennProsent(kjoennRatio);

        return new dataDTO(
                gjennomsnittligAlder,
                totaltAntallArtikler,
                new ArrayList<>(allePersoner),
                kjoennRatio,
                kjoennProsentFordeling,
                partiMentions,
                partiProsentFordeling,
                kilde);
    }

    /**
     * Beregner prosentvis fordeling av parti mentions
     * 
     * @param partiMentions Map med parti og antall artikler
     * @return Map med parti og prosent-andel
     */
    public static Map<String, Double> beregnPartiProsent(Map<String, Integer> partiMentions) {
        if (partiMentions == null || partiMentions.isEmpty()) {
            return new HashMap<>();
        }

        int totaltAntall = partiMentions.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totaltAntall == 0) {
            return new HashMap<>();
        }

        return partiMentions.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (entry.getValue() * 100.0) / totaltAntall));
    }

    /**
     * Beregner prosentvis fordeling av kjønn
     * 
     * @param kjoennRatio Map med kjønn og antall personer
     * @return Map med kjønn og prosent-andel
     */
    public static Map<String, Double> beregnKjoennProsent(Map<String, Integer> kjoennRatio) {
        if (kjoennRatio == null || kjoennRatio.isEmpty()) {
            return new HashMap<>();
        }

        int totaltAntall = kjoennRatio.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totaltAntall == 0) {
            return new HashMap<>();
        }

        return kjoennRatio.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (entry.getValue() * 100.0) / totaltAntall));
    }

}
