package VTPPday28.takehome.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;

import VTPPday28.takehome.service.BGGService;
import jakarta.json.JsonObject;

@Controller
@RequestMapping
public class BGGController {
    
    @Autowired
    private BGGService bggService;

    @GetMapping (path="/game/{game_id}/reviews", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getGameByGid(@PathVariable(name="game_id") int gid){
        JsonObject jObject = bggService.getDocumentById(gid);

        return ResponseEntity.ok().body(jObject.toString());

    }

    @GetMapping(path="/games/{req}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getGameByState(@PathVariable(name="req") String req){
        JsonObject jObject = bggService.getAllRatedGames(req);

        return ResponseEntity.ok().body(jObject.toString());
    }


}
