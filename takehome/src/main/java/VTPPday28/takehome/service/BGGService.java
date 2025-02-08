package VTPPday28.takehome.service;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VTPPday28.takehome.repository.BGGRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@Service
public class BGGService {
    @Autowired
    private BGGRepository bggRepository;

    public JsonObject getDocumentById(int gid){
        Document doc = bggRepository.getDocumentsById(gid);
        // System.out.println("\n\n\n" + doc);
        // System.out.println("\n\n\n" + doc.getDouble("average_rating"));

        List<Document> reviewList = doc.getList("reviews", Document.class);

        //build jsonarray for reviews
        JsonArrayBuilder jArray = Json.createArrayBuilder();
        reviewList.stream()
            .map(d ->(toJson(d)))
            .forEach(j -> jArray.add(j));


        //build jsonobject for everything
        JsonObjectBuilder jBuilder = Json.createObjectBuilder();
            jBuilder.add("game_id", doc.getInteger("gid"));
            jBuilder.add("name", doc.getString("name"));
            jBuilder.add("year", doc.getInteger("year"));
            jBuilder.add("rank", doc.getInteger("ranking"));
            jBuilder.add("average", doc.getDouble("average"));
            jBuilder.add("user_rated", doc.getInteger("users_rated"));
            jBuilder.add("url", doc.getString("url"));
            jBuilder.add("thumbnail", doc.getString("image"));
            jBuilder.add("reviews", jArray);
            jBuilder.add("timestamp", doc.getString("timestamp"));

        JsonObject jObj = jBuilder.build();
        
        return jObj;
    }


    public JsonObject getAllRatedGames(String req){
        //some games do not have reviews
        List<Document> docList = bggRepository.getAllRatedGames(req); //list of game and top/bottom review

        String bool = null;
        if(req.equals("highest")){
            bool = "highest";
        }else{
            bool = "lowest";
        }

        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        docList.stream()
            .map(d -> toJsonPartB(d))
            .forEach(j -> jArrayBuilder.add(j));

        JsonArray jArray = jArrayBuilder.build();

        //build final json object
        JsonObject endpoint = Json.createObjectBuilder()
            .add("rating", bool)
            .add("games", jArray)
            .add("timestamp", new Date().toString())
            .build();
        return endpoint;
    }



    public static JsonObject toJson(Document review){
        
        JsonObject jObj = Json.createObjectBuilder()
            .add("c_id", review.getString("c_id"))
            .add("user", review.getString("user"))
            .add("c_text", review.getString("c_text"))
            .build();
        return jObj;
    }

    public static JsonObject toJsonPartB(Document doc){
        //review value within each game json
        //could be empty
        List<Document> reviewList = doc.getList("reviews", Document.class);

        if (reviewList == null || reviewList.isEmpty()) {
            return Json.createObjectBuilder()
                .add("_id", "No comment ID")
                .add("name", "No name")
                .add("user", "No user available")
                .add("comment", "No comment available")
                .add("review_id", "No review available")
                .add("rating", "No rating available")
                .build();
        }

        JsonObject temp = Json.createObjectBuilder()
            .add("_id", doc.getInteger("gid", -1))
            .add("name", doc.getString("name"))
            .add("user", getOrDefault(reviewList, "user", "Unknown"))
            .add("comment", getOrDefault(reviewList, "c_text", "Unknown"))
            .add("review_id", getOrDefault(reviewList, "c_id", "Unknown"))
            .add("rating", reviewList.get(0).getInteger("rating", -1))
            .build();


        
        return temp;
    } 



    public static String getOrDefault(List<Document> docList, String key, String defaultValue) {
        if(docList != null && !docList.isEmpty() && docList.get(0) != null){
            return docList.get(0).getString(key);
        }else{
            return defaultValue;
        }
    }


}
