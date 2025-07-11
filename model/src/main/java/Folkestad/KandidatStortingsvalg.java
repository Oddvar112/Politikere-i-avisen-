package folkestad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "kandidat_stortingsvalg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KandidatStortingsvalg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "valg")
    private String valg;

    @Column(name = "valgdistrikt")
    private String valgdistrikt;

    @Column(name = "partikode")
    private String partikode;

    @Column(name = "partinavn")
    private String partinavn;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "kandidatnr")
    private Integer kandidatnr;

    @Column(name = "navn")
    private String navn;

    @Column(name = "bosted")
    private String bosted;

    @Column(name = "stilling")
    private String stilling;

    @Column(name = "foedselsdato")
    private LocalDate foedselsdato;

    @Column(name = "alder")
    private Integer alder;

    @Column(name = "kjoenn")
    private String kjoenn;
}
