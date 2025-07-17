package folkestad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString(exclude = "person") 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PersonLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 1000) 
    private String link;

    @Enumerated(EnumType.STRING)
    private Nettsted nettsted;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    /**
     * Sätter länken och identifierar automatiskt nettsted baserat på URL:en.
     *
     * @param link URL:en som ska sparas
     */
    public void setLinkAndDetectNettsted(String link) {
        this.link = link;
        this.nettsted = Nettsted.parseFromUrl(link).orElse(null);
    }

    /**
     * Skapar en PersonLink med länk och automatisk nettsted-identifiering.
     *
     * @param link URL:en
     * @param person Personen som länken tillhör
     * @return Ny PersonLink med nettsted automatiskt satt
     */
    public static PersonLink createWithDetectedNettsted(String link, Person person) {
        PersonLink personLink = new PersonLink();
        personLink.setLinkAndDetectNettsted(link);
        personLink.setPerson(person);
        return personLink;
    }
}
