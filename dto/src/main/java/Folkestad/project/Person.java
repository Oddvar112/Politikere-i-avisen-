package folkestad.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Person DTO med alle relevante data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private String navn;
    private Integer alder;
    private String kjoenn;
    private String parti;
    private String valgdistrikt;
    private List<ArtikelDTO> lenker;
    private int antallArtikler;
    
}
