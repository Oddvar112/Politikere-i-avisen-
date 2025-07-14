package folkestad.project.analysis;

import Folkestad.project.dataDTO;
import Folkestad.project.Person;
import java.util.*;
import java.util.stream.Collectors;

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
            String parti = (String) rad[1];
            Integer alder = (Integer) rad[2];
            String kjoenn = (String) rad[3];
            String valgdistrikt = (String) rad[4];
            String lenkerString = (String) rad[5];

            if (lenkerString != null && !lenkerString.isEmpty()) {
                String[] alleLenker = lenkerString.split(",");

                List<String> filtretteLenker = Arrays.stream(alleLenker)
                        .map(String::trim)
                        .filter(lenke -> kilde.equals("ALT") || lenke.contains(kilde))
                        .collect(Collectors.toList());

                if (!filtretteLenker.isEmpty()) {
                    Person person = new Person();
                    person.setNavn(navn);
                    person.setParti(parti);
                    person.setAlder(alder);
                    person.setKjoenn(kjoenn);
                    person.setValgdistrikt(valgdistrikt);
                    person.setAntallArtikler(filtretteLenker.size());
                    person.setLenker(filtretteLenker);

                    allePersoner.add(person);

                    partiMentions.merge(parti, filtretteLenker.size(), Integer::sum);

                    if (alder != null) {
                        aldersListe.add(alder);
                    }

                    if (kjoenn != null && !kjoenn.isEmpty()) {
                        kjoennRatio.merge(kjoenn, 1, Integer::sum);
                    }

                    totaltAntallArtikler += filtretteLenker.size();
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

    /**
     * Printer parti-fordeling med prosent
     * 
     * @param partiMentions Map med parti og antall artikler
     * @param kilde         Kildenavn for utskrift
     */
    public static void printPartiProsentFordeling(Map<String, Integer> partiMentions, String kilde) {
        Map<String, Double> partiProsent = beregnPartiProsent(partiMentions);

        System.out.println("\n=== PARTI FORDELING FOR " + kilde.toUpperCase() + " ===");
        partiProsent.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("%s: %.1f%% (%d artikler)%n",
                        entry.getKey(),
                        entry.getValue(),
                        partiMentions.get(entry.getKey())));
    }
}
