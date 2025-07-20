package folkestad.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SammendragDTO {
    private Long id;
    private String link;
    private String sammendrag;
    private Double kompresjonRatio;
    private Integer antallOrdOriginal;
    private Integer antallOrdSammendrag;
}