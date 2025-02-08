package VTTPday28.inclass.bootstrap;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import VTTPday28.inclass.repository.BreweriesRepository;

@Component
public class RunQueries implements CommandLineRunner {
    @Autowired
    private BreweriesRepository breweriesRepository;

    @Override
    public void run(String... arg){

      // List<Document> results = breweriesRepository.getBreweriesByCountry("germany");
      // List<Document> results = breweriesRepository.listBeersByStyle(); //THIS DOENT WORK
      List<Document> results = breweriesRepository.categorizeBeerByAlcoholVolume();

      for (Document d: results)
         System.out.printf(">>> %s\n\n", d);

      System.out.printf("Count: %d\n", results.size());

    }






}
