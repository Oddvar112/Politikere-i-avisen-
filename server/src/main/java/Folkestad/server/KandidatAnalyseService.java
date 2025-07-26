package folkestad.server;

import folkestad.project.dataDTO;
import folkestad.project.analysis.KandidateAnalysis;
import folkestad.project.analysis.KildeDataAnalyzer;
import folkestad.InnleggRepository;
import folkestad.project.SammendragDTO;
import folkestad.project.Person;
import folkestad.project.ArtikelDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for å håndtere HTTP requests til kandidat analyse data
 * Inneholder all business logikk og validering
 */
@Service
public class KandidatAnalyseService {
    
    @Autowired
    private KandidateAnalysis kandidateAnalysis;
    
    @Autowired
    private InnleggRepository innleggRepository;
    
    /**
     * Henter analyse data for spesifisert kilde med full validering og dato-filtrering
     * @param kilde Kilde å hente data for ("vg", "nrk", "e24", "alt")
     * @param fraDato Fra-dato for filtrering (null = ingen filtrering)
     * @param tilDato Til-dato for filtrering (null = ingen filtrering)
     * @return dataDTO for kilden, eventuelt filtrert
     * @throws IllegalStateException hvis data ikke er tilgjengelig
     * @throws IllegalArgumentException hvis ukjent kilde
     */
    public dataDTO getAnalyseDataForKilde(String kilde, LocalDateTime fraDato, LocalDateTime tilDato) {
        if (!kandidateAnalysis.erDataTilgjengelig()) {
            throw new IllegalStateException("Analyse data er ikke tilgjengelig");
        }
        
        dataDTO originalData = hentCachetData(kilde);
        
        if (fraDato == null || tilDato == null) {
            return originalData;
        }
        
        LocalDate fraLocalDate = fraDato.toLocalDate();
        LocalDate tilLocalDate = tilDato.toLocalDate();
        
        return filtrerData(originalData, fraLocalDate, tilLocalDate);
    }
    
    /**
     * Henter cachet data basert på kilde
     */
    private dataDTO hentCachetData(String kilde) {
        String normalizedKilde = kilde.toLowerCase().trim();
        switch (normalizedKilde) {
            case "vg":
                return kandidateAnalysis.getDataVG();
            case "nrk":
                return kandidateAnalysis.getDataNRK();
            case "e24":
                return kandidateAnalysis.getDataE24();
            case "alt":
            case "all":
                return kandidateAnalysis.getDataAlt();
            case "dagbladet":
                return kandidateAnalysis.getDataDagbladet();
            default:
                throw new IllegalArgumentException("Ukjent kilde: " + kilde);
        }
    }
    
    /**
     * Filtrerer allerede prosessert dataDTO basert på dato-intervall
     */
    private dataDTO filtrerData(dataDTO originalData, LocalDate fraDato, LocalDate tilDato) {
        List<Person> filtrertPersoner = originalData.getAllePersonernevnt().stream()
            .map(person -> filtrerPerson(person, fraDato, tilDato))
            .filter(Objects::nonNull)
            .filter(person -> !person.getLenker().isEmpty())
            .collect(Collectors.toList());
        return byggDataDTO(filtrertPersoner, originalData.getKilde());
    }
    
    /**
     * Filtrerer en enkelt person sine artikler basert på dato
     */
    private Person filtrerPerson(Person person, LocalDate fraDato, LocalDate tilDato) {
        
        List<ArtikelDTO> filtrertArtikler = person.getLenker().stream()
            .filter(artikkel -> !artikkel.getScraped().isBefore(fraDato) && 
                               !artikkel.getScraped().isAfter(tilDato))
            .collect(Collectors.toList());
        
        if (filtrertArtikler.isEmpty()) {
            return null;
        }
        
        return new Person(
            person.getNavn(),
            person.getAlder(), 
            person.getKjoenn(),
            person.getParti(),
            person.getValgdistrikt(),
            filtrertArtikler,
            filtrertArtikler.size()
        );
    }
    
    /**
     * Bygger ny dataDTO basert på filtrerte personer
     */
    private dataDTO byggDataDTO(List<Person> personer, String kilde) {
        if (personer.isEmpty()) {
            return new dataDTO(0.0, 0, new ArrayList<>(), new HashMap<>(), 
                             new HashMap<>(), new HashMap<>(), new HashMap<>(), kilde);
        }
        
        double gjennomsnittligAlder = personer.stream()
            .mapToInt(Person::getAlder)
            .average()
            .orElse(0.0);
        
        int totaltAntallArtikler = personer.stream()
            .mapToInt(Person::getAntallArtikler)
            .sum();
        
        Map<String, Integer> kjoennRatio = personer.stream()
            .collect(Collectors.groupingBy(
                Person::getKjoenn,
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        
        Map<String, Integer> partiMentions = personer.stream()
            .collect(Collectors.groupingBy(
                Person::getParti,
                Collectors.summingInt(Person::getAntallArtikler)
            ));
        
        Map<String, Double> kjoennProsentFordeling = KildeDataAnalyzer.beregnKjoennProsent(kjoennRatio);
        Map<String, Double> partiProsentFordeling = KildeDataAnalyzer.beregnPartiProsent(partiMentions);
        
        return new dataDTO(
            gjennomsnittligAlder,
            totaltAntallArtikler,
            new ArrayList<>(personer),
            kjoennRatio,
            kjoennProsentFordeling,
            partiMentions,
            partiProsentFordeling,
            kilde
        );
    }
    
    /**
     * Hent sammendrag for en gitt link
     * @param link artikkel-link
     * @return SammendragDTO eller null hvis ikke funnet
     */
    public SammendragDTO getSammendragForLink(String link) {
        return innleggRepository.findByLink(link)
            .map(innlegg -> new SammendragDTO(
                innlegg.getLink(),
                innlegg.getSammendrag(),
                innlegg.getKompresjonRatio(),
                innlegg.getAntallOrdOriginal(),
                innlegg.getAntallOrdSammendrag(),
                innlegg.getOpprettetDato()
            ))
            .orElse(null);
    }
}