package folkestad.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import folkestad.project.SammendragDTO;
import folkestad.project.dataDTO;

/**
 * REST Controller for kandidat analyse endpoints
 * Håndterer kun HTTP requests og delegerer til service
 */
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://localhost:5173", 
    "https://kvasirsbrygg.no",
    "https://api.kvasirsbrygg.no"
})
@RestController
@RequestMapping("/api/analyse")
public class KandidatAnalyseController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KandidatAnalyseController.class);
    
    @Autowired
    private KandidatAnalyseService kandidatAnalyseService;
        
    /**
     * Henter analyse data for spesifisert kilde
     * GET /api/analyse/{kilde}
     */
    @GetMapping("/{kilde}")
    public ResponseEntity<dataDTO> getAnalyseData(@PathVariable("kilde") String kilde) {
        try {
            dataDTO result = kandidatAnalyseService.getAnalyseDataForKilde(kilde);
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Hent sammendrag for en gitt link
     * GET /api/analyse/sammendrag?link=...
     */
    @GetMapping("/sammendrag")
    public ResponseEntity<SammendragDTO> getSammendragForLink(@RequestParam("link") String link) {
        LOGGER.info("=== SAMMENDRAG REQUEST ===");
        LOGGER.info("Mottatt link: '{}'", link);
        LOGGER.info("Link lengde: {}", link != null ? link.length() : "null");
        
        try {
            SammendragDTO dto = kandidatAnalyseService.getSammendragForLink(link);
            
            if (dto == null) {
                LOGGER.warn("Ingen sammendrag funnet for link: '{}'", link);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            LOGGER.info("Sammendrag funnet! ID: {}", dto.getId());
            return ResponseEntity.ok(dto);
            
        } catch (Exception e) {
            LOGGER.error("Feil ved henting av sammendrag for link '{}': {}", link, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}