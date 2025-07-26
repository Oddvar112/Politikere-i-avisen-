package folkestad.project;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtikelDTO {
    
    private String lenke;
    private LocalDate scraped;
  }

