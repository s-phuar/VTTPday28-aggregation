package vttp.batch5.paf.day28.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Repository
public class GamesRepository {
    @Autowired
    private MongoTemplate template;

    public List<JsonObject> getGames(String search){
        Criteria criteria = Criteria.where("name")
            .regex(search, "i");

        MatchOperation filterByName = Aggregation.match(criteria);

        ProjectionOperation projectDetails = Aggregation.project("gid", "name", "ranking", "url", "image")
            .andExclude("_id");


        Aggregation pipeline = Aggregation.newAggregation(filterByName, projectDetails);
        AggregationResults<Document> results = template.aggregate(pipeline, "games", Document.class);

        //returns list of games that can be mapped to a games object
        return results.getMappedResults().stream()
            .map(doc -> Json.createObjectBuilder()
                .add("gid", doc.getInteger("gid"))
                .add("name", doc.getString("name"))
                .add("ranking", doc.getInteger("ranking"))
                .add("url", doc.getString("url"))
                .add("image", doc.getString("image"))
                .build()
            ).toList();



    }
    
}
