package Folkestad.Project;

import java.util.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * CoreNLPProcessor håndterer all Stanford CoreNLP-funksjonalitet for navnegjenkjenning.
 * Denne klassen er ansvarlig for å konfigurere og kjøre CoreNLP pipeline samt 
 * ekstrahering av person-entiteter fra tekst.
 */
public class CoreNLPProcessor {
    private static final String PIPELINE_COMPONENTS = "tokenize, ssplit, pos, lemma, ner";
    private final StanfordCoreNLP pipeline;

    /**
     * Konstruktør som oppretter og konfigurerer CoreNLP pipeline.
     */
    public CoreNLPProcessor() {
        this.pipeline = createPipeline();
    }

    /**
     * Ekstraherer kandidatnavn fra en tekst ved hjelp av CoreNLP.
     * 
     * @param text Teksten som skal analyseres
     * @return Liste med navn funnet av CoreNLP som PERSON-entiteter
     */
    public List<String> extractPersonNames(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        
        List<String> names = new ArrayList<>();
        StringBuilder currentName = new StringBuilder();
        
        for (CoreLabel token : document.tokens()) {
            String entity = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            String word = token.originalText();
            
            if ("PERSON".equals(entity)) {
                if (currentName.length() > 0) {
                    currentName.append(" ");
                }
                currentName.append(word);
            } else {
                if (currentName.length() > 0) {
                    names.add(currentName.toString());
                    currentName.setLength(0);
                }
            }
        }
        
        // Legg til siste navn hvis det ikke ble lagt til i løkken
        if (currentName.length() > 0) {
            names.add(currentName.toString());
        }
        
        return names;
    }

    /**
     * Oppretter og konfigurerer StanfordCoreNLP pipeline.
     * 
     * @return Konfigurert StanfordCoreNLP pipeline
     */
    private StanfordCoreNLP createPipeline() {
        Properties props = new Properties();
        props.setProperty("annotators", PIPELINE_COMPONENTS);
        props.setProperty("coref.algorithm", "neural");
        return new StanfordCoreNLP(props);
    }

    /**
     * Lukker pipeline og frigjør ressurser.
     * Bør kalles når CoreNLPProcessor ikke lenger skal brukes.
     */
    public void close() {
        if (pipeline != null) {
            // CoreNLP har ikke eksplisitt close() metode, men vi kan sette referansen til null
            // for å hjelpe garbage collector
        }
    }
}
