package vttp.batch5.paf.day28.services;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.batch5.paf.day28.models.Comment;
import vttp.batch5.paf.day28.models.Game;
import vttp.batch5.paf.day28.repositories.BGGRepository;


@Service
public class BGGService {
    @Autowired
    private BGGRepository bggRepository;

    //service, grabs list of games documents -> map to games object
    //use games id to grab list of comments document -> map to comment document -> add to games object
    

    public List<Game> findGamesAndComments(String name){
        List<Document> results = bggRepository.findGamesAndComments(name);
        List<Game> games = new LinkedList<>();
        for(Document gDoc: results){
            Game game = new Game();
            game.setGameId(gDoc.getInteger("gid"));
            game.setName(gDoc.getString("name"));
            game.setRanking(gDoc.getInteger("ranking"));
            game.setUrl(gDoc.getString("url"));
            game.setImage(gDoc.getString("image"));

            for(Document cDoc: gDoc.getList("comments",Document.class)){
                Comment comment = new Comment();
                comment.setText(cDoc.getString("c_text"));
                comment.setUser(cDoc.getString("user"));
                comment.setRating(cDoc.getInteger("rating"));
                game.addComment(comment); //when game object is created, Linked list for List<Comment> is created as "comments" (see class for addComment method)
            }
            games.add(game);
        }
        return games;
    }




}
