package folkestad.project.analysis;

import Folkestad.project.dataDTO;
import folkestad.KandidatLinkRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for å håndtere kandidat analyse med caching
 * Holder analysedata for VG, NRK, E24 og samlet (ALT)
 */
@Service
@Getter
public class KandidateAnalysis {

    @Autowired
    private KandidatLinkRepository kandidatLinkRepository;
    
    private dataDTO dataVG;
    private dataDTO dataNRK;
    private dataDTO dataE24;
    private dataDTO dataAlt;
    
    private LocalDateTime sistOppdatert;
    
    /**
     * Utfører kandidat analyse og lager cache
     * Henter data fra database og filtrerer per kilde
     */
    public void analyzeKandidatData() {
        List<Object[]> rawData = kandidatLinkRepository.findKandidatNavnWithLinks();
        
        dataVG = KildeDataAnalyzer.analyzeDataByKilde(rawData, "vg.no");
        dataNRK = KildeDataAnalyzer.analyzeDataByKilde(rawData, "nrk.no");
        dataE24 = KildeDataAnalyzer.analyzeDataByKilde(rawData, "e24.no");
        dataAlt = KildeDataAnalyzer.analyzeDataByKilde(rawData, "ALT");
        
        sistOppdatert = LocalDateTime.now();
    }
    
    /**
     * Oppdaterer analyse-dataene fra database
     */
    public void oppdater() {
        analyzeKandidatData();
    }

    /**
     * Sjekker om data er tilgjengelig
     * @return true hvis analyse er kjørt
     */
    public boolean erDataTilgjengelig() {
        return dataAlt != null;
    }
}
