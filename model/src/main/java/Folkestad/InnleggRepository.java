package folkestad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InnleggRepository extends JpaRepository<Innlegg, Long> {
    Optional<Innlegg> findByLink(String link);
    
    @Query("SELECT i FROM Innlegg i WHERE i.link = :baseUrl OR i.link LIKE CONCAT(:baseUrl, '?%')")
    Optional<Innlegg> findByNormalizedUrl(@Param("baseUrl") String baseUrl);
    
    boolean existsByLink(String link);
    List<Innlegg> findBySammendragIsNotNull();
    long countBySammendragIsNotNull();

}
