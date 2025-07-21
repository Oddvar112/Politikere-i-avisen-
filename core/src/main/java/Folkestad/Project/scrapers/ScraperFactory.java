package folkestad.project.scrapers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import folkestad.InnleggRepository;

@Component
public class ScraperFactory {

    @Autowired
    private InnleggRepository innleggRepository;


    public NRKScraper createNRKScraper(String url) {
        NRKScraper scraper = new NRKScraper(url);
        scraper.setInnleggRepository(innleggRepository);
        return scraper;
    }

 
    public VGScraper createVGScraper(String url) {
        VGScraper scraper = new VGScraper(url);
        scraper.setInnleggRepository(innleggRepository);
        return scraper;
    }

    public E24Scraper createE24Scraper(String url) {
        E24Scraper scraper = new E24Scraper(url);
        scraper.setInnleggRepository(innleggRepository);
        return scraper;
    }

    public DagbladetScraper createDagbladetScraper(String url) {
        DagbladetScraper scraper = new DagbladetScraper(url);
        scraper.setInnleggRepository(innleggRepository);
        return scraper;
    }
}
