package folkestad;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@Entity
@Table(name = "innlegg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Innlegg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "link", length = 1000, nullable = false, unique = true)
    private String link;

    @Lob
    @Column(name = "sammendrag", columnDefinition = "TEXT")
    private String sammendrag;

    @Column(name = "kompresjon_ratio")
    private Double kompresjonRatio;

    @Column(name = "antall_ord_original")
    private Integer antallOrdOriginal;

    @Column(name = "antall_ord_sammendrag")
    private Integer antallOrdSammendrag;

    
    @Column(name = "opprettet_dato")
    @Builder.Default
    private LocalDateTime opprettetDato = LocalDateTime.now();

    public void calculateCompressionRatio() {
        if (antallOrdOriginal != null && antallOrdSammendrag != null && antallOrdOriginal > 0) {
            this.kompresjonRatio = (double) antallOrdSammendrag / antallOrdOriginal;
        }
    }

    public void setSammendragWithStats(String sammendrag, String originalTekst) {
        this.sammendrag = sammendrag;
        
        if (sammendrag != null) {
            this.antallOrdSammendrag = sammendrag.split("\\s+").length;
        }
        
        if (originalTekst != null) {
            this.antallOrdOriginal = originalTekst.split("\\s+").length;
        }
        
        calculateCompressionRatio();
    }
}