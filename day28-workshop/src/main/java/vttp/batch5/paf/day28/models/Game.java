package vttp.batch5.paf.day28.models;

import java.util.List;

import jakarta.json.JsonObject;

import java.util.LinkedList;

public class Game {

   private int gameId; // gid
   private String name; // name
   private int ranking; // ranking
   private String url; // url
   private String image; // image
   private List<Comment> comments = new LinkedList<>();

   public int getGameId() { return gameId; }
   public void setGameId(int gameId) { this.gameId = gameId; }

   public String getName() { return name; }
   public void setName(String name) { this.name = name; }

   public int getRanking() { return ranking; }
   public void setRanking(int ranking) { this.ranking = ranking; }

   public String getUrl() { return url; }
   public void setUrl(String url) { this.url = url; }

   public String getImage() { return image; }
   public void setImage(String image) { this.image = image; }

   public List<Comment> getComments() { return this.comments; }
   public void setComments(List<Comment> comments) { this.comments = comments; }
   public void addComment(Comment comment) { this.comments.add(comment); }

   @Override
   public String toString() {
      return "Game [gameId=" + gameId + ", name=" + name + ", ranking=" + ranking + "]";
   }

   public Game(int gameId, String name, int ranking, String url, String image) {
      this.gameId = gameId;
      this.name = name;
      this.ranking = ranking;
      this.url = url;
      this.image = image;
   }

   public static Game toGame(JsonObject jObj){
      return new Game(jObj.getInt("gid"), jObj.getString("name"), jObj.getInt("ranking"), jObj.getString("url"), jObj.getString("image"));
   }
   


}
