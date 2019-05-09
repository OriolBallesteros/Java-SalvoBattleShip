package salvo.salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;
    private String type;


    @ElementCollection
    @Column(name="shipLocation")
    private List<String> shipLocation = new ArrayList<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;


    public Ship(){  }

    public Ship (String type, List<String> location, GamePlayer gamePlayer){
        this.type = type;
        this.shipLocation = location;
        this.gamePlayer = gamePlayer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocation() {
        return shipLocation;
    }

    public void setLocation(List<String> location) {
        this.shipLocation = location;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
