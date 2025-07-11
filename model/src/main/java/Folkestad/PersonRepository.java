package folkestad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByName(String name);

    @Query("SELECT p FROM Person p LEFT JOIN FETCH p.links WHERE p.name IN :names")
    List<Person> findByNameInWithLinks(@Param("names") List<String> names);
}
