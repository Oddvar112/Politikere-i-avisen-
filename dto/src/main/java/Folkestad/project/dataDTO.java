package folkestad.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

/**
 * DTO for å holde analyse-data per kilde (VG, NRK, E24, eller samlet)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class dataDTO {

    private double gjennomsnittligAlder;
    private int totaltAntallArtikler;
    private ArrayList<Person> allePersonernevnt;
    private Map<String, Integer> kjoennRatio;
    private Map<String, Double> kjoennProsentFordeling; 
    private Map<String, Integer> partiMentions;
    private Map<String, Double> partiProsentFordeling; 
    private String kilde;

}
