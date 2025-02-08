package vttp.batch5.paf.day28.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
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

    public List<Document> findGamesAndComments(String name){
        Criteria criteria = Criteria.where("name")
            .regex(name, "i");

        MatchOperation filterGameByName = Aggregation.match(criteria);

        //project the fields
        ProjectionOperation projectFields = Aggregation.project("gid", "name", "ranking", "url", "image")
            .andExclude("_id");

        //lookup the comments of a game
        ProjectionOperation projectComments = Aggregation.project("user", "C_text", "rating").andExclude("_id");
        SortOperation sortCommentsByRating = Aggregation.sort(Sort.Direction.DESC, "rating");
        LimitOperation top5Comments = Aggregation.limit(5);
        LookupOperation joinCommentsByGid = LookupOperation.newLookup()
            .from("comments")
            .localField("gid").foreignField("gid")
            .pipeline(projectComments, sortCommentsByRating, top5Comments)
            .as("comments");



        //sort games by ranking, best at the top, use asc
        SortOperation sortGamesByRanking = Aggregation.sort(Sort.Direction.ASC, "ranking");

        //create the pipeline
        Aggregation pipeline = Aggregation.newAggregation(filterGameByName, projectFields, joinCommentsByGid,  sortGamesByRanking);
        
        
        
        return template.aggregate(pipeline, "games", Document.class).getMappedResults();
    }

}
