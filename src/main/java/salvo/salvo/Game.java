package salvo.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;
    private Date dateCreationGame;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();


    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    //@Fetch(value = FetchMode.SUBSELECT)
    private Set<Score> scores = new LinkedHashSet<>();





    public Game (){
        this.dateCreationGame = new Date();
    }

    public Date getDate (){
        return this.dateCreationGame;
    }

    public void setDate (Date settingDate){
        this.dateCreationGame = settingDate;
    }

    public void addGamePlayers (GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }


    @JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers
                .stream()
                .map(sub -> sub.getPlayer())
                .collect(Collectors.toList());
    }


    public long getId() {
        return id;
    }


    public Map<String, Object> toDto (){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", getId());
        dto.put("create", new Date());
        return dto;
    }

    public Set<GamePlayer> getGamePlayers() {
        return this.gamePlayers;
    }


    public Set<Score> getScores() {
        return scores;
    }

}

