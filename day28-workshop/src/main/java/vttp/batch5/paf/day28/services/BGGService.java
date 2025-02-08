package vttp.batch5.paf.day28.services;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp.batch5.paf.day28.models.Comment;
import vttp.batch5.paf.day28.models.Game;
import vttp.batch5.paf.day28.repositories.CommentsRepository;
import vttp.batch5.paf.day28.repositories.GamesRepository;

@Service
public class BGGService {
    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private GamesRepository gamesRepository;


    //service, grabs list of games documents -> map to games object
    //use games id to grab list of comments document -> map to comment document -> add to games object
    






}
