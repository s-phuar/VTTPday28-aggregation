package vttp.batch5.paf.day28.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Repository
public class CommentsRepository {
    @Autowired
    private MongoTemplate template;

    public List<JsonObject> getCommentsByGid(int gid){

        Criteria criteria = Criteria.where("gid").is(gid);

        MatchOperation filterByGid = Aggregation.match(criteria);

        ProjectionOperation projectDetails = Aggregation.project("user", "rating", "c_text")
            .andExclude("_id");

        SortOperation byRank = Aggregation.sort(Sort.Direction.DESC, "rating");

        LimitOperation limit = Aggregation.limit(5);

        Aggregation pipeline = Aggregation.newAggregation(filterByGid, projectDetails, byRank, limit);

        AggregationResults<Document> results = template.aggregate(pipeline, "comments", Document.class);

        return results.getMappedResults().stream()
            .map(doc -> Json.createObjectBuilder()
                .add("user", doc.getString("user"))
                .add("rating", doc.getInteger("rating"))
                .add("c_text", doc.getString("c_text"))
                .build()
            ).toList();
    }
    
    




}
