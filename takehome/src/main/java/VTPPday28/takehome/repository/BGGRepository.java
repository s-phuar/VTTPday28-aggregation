package VTPPday28.takehome.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class BGGRepository {
    @Autowired
    private MongoTemplate template;

/*
db.games.aggregate([
    {
        "$match": { "gid": 161936 }  // Step 1: Find the game by ID
    },
    {
        "$lookup": {
            "from": "comments",
            "localField": "gid",
            "foreignField": "gid",
            "as": "reviews"
        }
    },
    {
        "$addFields": {
            "average_rating": { "$avg": "$reviews.rating" },  // Step 3: Compute Average Rating
            "timestamp":{$dateToString:{format: "%Y-%m-%d %H:%M:%S", date:"$$NOW", timezone:"Singapore"}}
        }
    }
])

  
 */

    public Document getDocumentsById(int gid){
        Criteria criteria = Criteria.where("gid").is(gid);
        MatchOperation filter = Aggregation.match(criteria);

        LookupOperation lookupBeers = LookupOperation.newLookup()
        .from("comments")
        .localField("gid").foreignField("gid")
        .as("reviews");

        AddFieldsOperation addFieldsOperation = Aggregation.addFields()
            .addField("average").withValue(new Document("$avg", "$reviews.rating"))
            .addField("timestamp").withValue(new Document("$dateToString", new Document()
                    .append("format", "%Y-%m-%d %H:%M:%S")
                    .append("date", "$$NOW")
                    .append("timezone", "Asia/Singapore")
            ))
            .build();

        Aggregation pipeline = Aggregation.newAggregation(filter, lookupBeers, addFieldsOperation);
        AggregationResults<Document> results = template.aggregate(pipeline, "games", Document.class);

        return results.getUniqueMappedResult();
    }


    public List<Document> getAllRatedGames(String req){
        System.out.println("reached repo");

        ProjectionOperation projectComments = Aggregation.project("user", "c_text", "rating", "c_id")
            .andExclude("_id");
        SortOperation sortCommentsByRating = null;

        if(req.equals("highest")){
            sortCommentsByRating = Aggregation.sort(Sort.Direction.DESC, "rating");
        }else{
            sortCommentsByRating = Aggregation.sort(Sort.Direction.ASC, "rating");
        }
        LimitOperation top1Comment = Aggregation.limit(1);

        LookupOperation lookUp = LookupOperation.newLookup()
            .from("comments")
            .localField("gid").foreignField("gid")
            .pipeline(projectComments, sortCommentsByRating, top1Comment)
            .as("reviews");


        Aggregation pipeline = Aggregation.newAggregation(lookUp);
        AggregationResults<Document> results = template.aggregate(pipeline, "games", Document.class);
        System.out.println("end of repo");
        return results.getMappedResults();
    }






}
