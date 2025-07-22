package folkestad.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SammendragDTO {
    private String link; 
    private String sammendrag;
    private Double kompresjonRatio;
    private Integer antallOrdOriginal;
    private Integer antallOrdSammendrag;
    private LocalDateTime opprettetDato;
}