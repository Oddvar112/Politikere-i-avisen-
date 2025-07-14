package folkestad.server;

import Folkestad.project.dataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for kandidat analyse endpoints
 * Håndterer kun HTTP requests og delegerer til service
 */
@RestController
@RequestMapping("/api/analyse")
public class KandidatAnalyseController {
    
    @Autowired
    private KandidatAnalyseService kandidatAnalyseHttpService;
    
    /**
     * Henter analyse data for spesifisert kilde
     * GET /api/analyse/{kilde}
     */
    @GetMapping("/{kilde}")
    public ResponseEntity<dataDTO> getAnalyseData(@PathVariable String kilde) {
        try {
            dataDTO result = kandidatAnalyseHttpService.getAnalyseDataForKilde(kilde);
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
