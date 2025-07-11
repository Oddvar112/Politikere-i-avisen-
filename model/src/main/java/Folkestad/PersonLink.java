package Folkestad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "person") // Avoid circular reference in toString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PersonLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String link;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}
