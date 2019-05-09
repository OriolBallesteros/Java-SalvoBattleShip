package salvo.salvo;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

import static java.util.stream.Collectors.toList;


@Entity
public class GamePlayer {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;
    private Date date;


    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "player_id")
    private Player player;


    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "game_id")
    private Game game;


    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> ships;


    @OneToMany(mappedBy ="gamePlayer", fetch=FetchType.EAGER)

    private Set<Salvo> salvos = new LinkedHashSet<>();


    public GamePlayer (){ }

    public GamePlayer (Game gamePlaying, Player playerPlaying){
        this.game = gamePlaying;
        this.player = playerPlaying;
        this.date = new Date();
    }

    public void setGame(Game game){
        this.game = game;
    }

    public Game getGame (){
        return this.game;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public Player getPlayer (){
        return this.player;
    }


    public long getId() {
        return id;
    }




    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public void addShips (Ship ship){
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvos (Salvo salvo){
        salvo.setGamePlayer(this);
        salvos.add(salvo);
    }

    public Set<Salvo> getSalvos() { return salvos; }

    public void setSalvos(Set<Salvo> salvos) { this.salvos = salvos; }

    public Score getScore(){
        return player.getScore(game);
    }

}
