package folkestad.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import folkestad.project.SammendragDTO;
import folkestad.project.dataDTO;

import java.time.LocalDateTime;

/**
 * REST Controller for kandidat analyse endpoints
 * Håndterer kun HTTP requests og delegerer til service
 */
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://localhost:5173", 
    "https://kvasirsbrygg.no",
    "https://www.kvasirsbrygg.no", // <-- legg til denne!
    "https://api.kvasirsbrygg.no"
})
@RestController
@RequestMapping("/api/analyse")
public class KandidatAnalyseController {
    
    @Autowired
    private KandidatAnalyseService kandidatAnalyseService;
        
    /**
     * Henter analyse data for spesifisert kilde med valgfri dato-filtrering
     * GET /api/analyse/{kilde}?fraDato=2025-01-01T00:00:00&tilDato=2025-01-31T23:59:59
     */
    @GetMapping("/{kilde}")
    public ResponseEntity<dataDTO> getAnalyseData(
            @PathVariable("kilde") String kilde,
            @RequestParam(value = "fraDato", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fraDato,
            @RequestParam(value = "tilDato", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tilDato) {
        
        try {
            dataDTO result = kandidatAnalyseService.getAnalyseDataForKilde(kilde, fraDato, tilDato);
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
        SammendragDTO dto = kandidatAnalyseService.getSammendragForLink(link);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dto);
    }
}