package salvo.salvo;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
public class Player {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;

    private String userName;

    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();


    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores = new LinkedHashSet<>();

    public Player (){    }

    public Player (String mail, String password){
        this.userName = mail;
        this.password = password;
    }

    public String getUserName (){
        return this.userName;
    }

    public void setUserName (String newUserName){
        this.userName = newUserName;
    }

    public String toString(){
        return "Current user: "+this.userName;
    }



    public void addGamePlayers (GamePlayer gamePlayer){
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public List<Game> getGames() {
        return gamePlayers
                .stream()
                .map(sub -> sub.getGame())
                .collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }


    public Set<Score> getScores() {
        return scores;
    }

    public Score getScore(Game game) {
        return scores.stream()
                .filter(p -> p.getGame().equals(game))
                .findFirst()
                .orElse(null);
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
