package VTTPday28.inclass.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.BucketOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;

@Repository
public class BreweriesRepository {
 
    @Autowired
    private MongoTemplate template;
    
    // db.breweries.aggregate([
    //     {$match:{coutry:{$regex:"country", $options:"i"}}},
    //     {$project:{_id:0, name:1, address1:1, city:1}},
    //     {$group:{_id:"$city", breweries:{$push {name:"name", address:"$address1"}}}},
    //     {$sort:{_id:1}}
    // ])
    public List<Document> getBreweriesByCountry(String country){
        //create one or more pipeline stages
        //create a $match - MatchOperation
        Criteria criteria = Criteria.where("country")
            .regex(country, "i");
        MatchOperation filterBycountry = Aggregation.match(criteria);

        //project name, address1, city
        ProjectionOperation projectDetails = Aggregation.project("name", "address1", "city")
            .andExclude("_id");

        // Group breweries by the city
        // { $group: { _id: '$city', breweries: { $push: '$name' } } }
        // GroupOperation byCity = Aggregation.group("city")
        //       .push("name").as("breweries")
        //       .count().as("count");

        // {$group:{_id:"$city", breweries:{$push:{name:"$name", addrress:"$address1"}}, count:{$sum:1}}},
        GroupOperation byCity = Aggregation.group("city")
            .push(
                new BasicDBObject()
                    .append("name", "$name")
                    .append("address", "$address1")
            ).as("breweries")
            .count().as("count");

        //sort the city
        SortOperation sortBycity = Aggregation.sort(Sort.Direction.ASC, "_id");

        //assemble our pipeline
        Aggregation pipeline = Aggregation.newAggregation(filterBycountry, projectDetails, byCity, sortBycity);

        //execute the pipeline
        AggregationResults<Document> results = template.aggregate(pipeline, "breweries", Document.class);

        //returns a list
        return results.getMappedResults();
    }

    //list all the beers under a specific style
    // db.styles.aggregate([
    // {$lookup:{from:"beers", foreignField:"style_id", localField:"id", as:"beers", pipeline:[{$project:{_id:0, name:1}}]}}
    // ])
    public List<Document> listBeersByStyle(){

        //project the beer name only
        ProjectionOperation beerName = Aggregation.project("name")
            .andExclude("_id");

        //lookup operation
        //LookupOperation lookupBeers = Aggregation.lookup("beers","id", "style_id", "beers");
        LookupOperation lookupBeers = LookupOperation.newLookup()
            .from("beers")
            .localField("id").foreignField("style_id")
            .pipeline(beerName)
            .as("beers");

        //create a pipeline
        Aggregation pipeline = Aggregation.newAggregation(lookupBeers);

        //execute the pipeline
        return template.aggregate(pipeline, "styles", Document.class).getMappedResults();
    }

   /*
    * db.beers.aggregate([
         {
            $bucket: {
               groupBy: '$abv',
               boundaries: [ 0, 3, 5, 7, 9, 15 ],
               default: '>=15',
               output: {
                count:{$sum:1},
                  beers: { $push: "$name" }
               }
            }
         }
      ])
    */
    public List<Document> categorizeBeerByAlcoholVolume(){
        BucketOperation alcoholByVolume = Aggregation.bucket("abv")
            .withBoundaries(0, 3, 5, 7, 9, 15)
            .withDefaultBucket(">=15")
            .andOutput("name").push().as("beers")
            .andOutputCount().as("count"); //count number of beers in each document

        Aggregation pipeline = Aggregation.newAggregation(alcoholByVolume);

        return template.aggregate(pipeline, "beers", Document.class).getMappedResults();
    }








    


}
