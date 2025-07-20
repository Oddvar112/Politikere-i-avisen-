package folkestad.server;

import folkestad.project.dataDTO;
import folkestad.project.analysis.KandidateAnalysis;
import folkestad.InnleggRepository;
import folkestad.project.SammendragDTO;

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
     * Henter analyse data for spesifisert kilde med full validering
     * @param kilde Kilde å hente data for ("vg", "nrk", "e24", "alt")
     * @return dataDTO for kilden
     * @throws IllegalStateException hvis data ikke er tilgjengelig
     * @throws IllegalArgumentException hvis ukjent kilde
     */
    public dataDTO getAnalyseDataForKilde(String kilde) {
        if (!kandidateAnalysis.erDataTilgjengelig()) {
            throw new IllegalStateException("Analyse data er ikke tilgjengelig");
        }
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
            default:
                throw new IllegalArgumentException("Ukjent kilde: " + kilde);
        }
    }
    
    /**
     * Hent sammendrag for en gitt link
     * @param link artikkel-link
     * @return SammendragDTO eller null hvis ikke funnet
     */
    public SammendragDTO getSammendragForLink(String link) {
        return innleggRepository.findByLink(link)
            .map(innlegg -> new SammendragDTO(
                innlegg.getId(),
                innlegg.getLink(),
                innlegg.getSammendrag(),
                innlegg.getKompresjonRatio(),
                innlegg.getAntallOrdOriginal(),
                innlegg.getAntallOrdSammendrag()
            ))
            .orElse(null);
    }
}
