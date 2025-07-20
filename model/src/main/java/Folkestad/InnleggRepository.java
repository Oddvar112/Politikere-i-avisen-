package folkestad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InnleggRepository extends JpaRepository<Innlegg, Long> {
    Optional<Innlegg> findByLink(String link);
    boolean existsByLink(String link);
    List<Innlegg> findBySammendragIsNotNull();
    long countBySammendragIsNotNull();
    
}