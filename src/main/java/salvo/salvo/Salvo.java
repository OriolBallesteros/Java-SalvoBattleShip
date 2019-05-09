package salvo.salvo;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    private Integer turn;

    @ElementCollection
    @Column (name="salvoLocation")
    private List<String> salvoLocation = new ArrayList<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;



    public Salvo(){  }

    public Salvo (GamePlayer gamePlayer, Integer turn, List<String> salvoLocation){
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocation = salvoLocation;

    }

    public Integer getTurn() { return turn; }

    public void setTurn(Integer turn) { this.turn = turn; }

    public List<String> getSalvoLocation() { return salvoLocation; }

    public void setSalvoLocation(List<String> salvoLocation) { this.salvoLocation = salvoLocation; }

    public GamePlayer getGamePlayer() { return gamePlayer; }

    public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }
}

