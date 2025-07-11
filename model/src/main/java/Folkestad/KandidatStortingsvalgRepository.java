package Folkestad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KandidatStortingsvalgRepository extends JpaRepository<KandidatStortingsvalg, Long> {
    
    /**
     * Finn alle kandidater for et bestemt valgdistrikt
     */
    List<KandidatStortingsvalg> findByValgdistrikt(String valgdistrikt);
    
    /**
     * Finn alle kandidater for et bestemt parti
     */
    List<KandidatStortingsvalg> findByPartikode(String partikode);
    
    /**
     * Finn alle kandidater for et bestemt parti og valgdistrikt
     */
    List<KandidatStortingsvalg> findByPartikodeAndValgdistrikt(String partikode, String valgdistrikt);
    
    /**
     * Søk etter kandidater basert på navn
     */
    @Query("SELECT k FROM KandidatStortingsvalg k WHERE LOWER(k.navn) LIKE LOWER(CONCAT('%', :navn, '%'))")
    List<KandidatStortingsvalg> findByNavnContainingIgnoreCase(@Param("navn") String navn);
    
    /**
     * Finn alle unike valgdistrikter
     */
    @Query("SELECT DISTINCT k.valgdistrikt FROM KandidatStortingsvalg k ORDER BY k.valgdistrikt")
    List<String> findAllDistinctValgdistrikter();
    
    /**
     * Finn alle unike partier
     */
    @Query("SELECT DISTINCT k.partikode FROM KandidatStortingsvalg k ORDER BY k.partikode")
    List<String> findAllDistinctPartikoder();
}
