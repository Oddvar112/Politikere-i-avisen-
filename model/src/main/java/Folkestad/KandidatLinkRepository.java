package folkestad;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KandidatLinkRepository extends JpaRepository<KandidatLink, Long> {
    
    /**
     * SQL-basert gruppering som returnerer alle lenker per kandidat.
     * Returnerer én rad per kandidat med alle lenker som kommaseparert streng.
     * @return Liste med kandidatnavn og deres samlede lenker
     */
    @Query(value = "SELECT ks.navn, " +
                   "GROUP_CONCAT(kl.link ORDER BY kl.link SEPARATOR ',') as alle_lenker " +
                   "FROM kandidat_link kl " +
                   "JOIN kandidat_stortingsvalg ks ON kl.kandidat_navn = ks.navn " +
                   "GROUP BY ks.navn " +
                   "ORDER BY ks.navn", 
           nativeQuery = true)
    List<Object[]> findKandidatNavnWithLinks();
}
