package vttp.batch5.paf.day28;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vttp.batch5.paf.day28.repositories.BGGRepository;

@SpringBootApplication
public class Day28Application implements CommandLineRunner{

	@Autowired
	private BGGRepository bggRepository;
	public static void main(String[] args) {
		SpringApplication.run(Day28Application.class, args);
	}


	@Override
	public void run(String... args){
		List<Document> results = bggRepository.findGamesAndComments("twilight");

		for(Document d: results){
			System.out.printf(">>> %d\n\n", d);
		}

	}

}
