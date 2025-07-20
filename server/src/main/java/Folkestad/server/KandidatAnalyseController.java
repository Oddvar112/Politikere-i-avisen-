package folkestad.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
