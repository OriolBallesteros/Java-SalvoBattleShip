package salvo.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getGamesInfo(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("game", gameRepo
                            .findAll()
                            .stream()
                            .map(game -> makeGamesDTO(game))
                            .collect(toList()));

        dto.put("leaderBoard", playerRepository
                                .findAll()
                                .stream()
                                .map(player -> makeLeaderBoardDTO(player))
                                .collect(toList()));

        if (authentication != null) {
            dto.put("player", authPlayerDTO(authentication));
        } else {
            dto.put("error", "No logged in player");
        }

        return dto;
    }

    private Map<String, Object> makeGamesDTO (Game game){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Set<GamePlayer> gamePlayers = game.getGamePlayers();
        dto.put("id", game.getId());
        dto.put("create", game.getDate());
        dto.put("gamePlayers", gamePlayers.stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(toList()));

        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("id", gamePlayer.getId());
            dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        if (gamePlayer.getScore() != null) {
            dto.put("score", gamePlayer.getScore().getScore());
        }
        else{
            dto.put("score", null);
        }

        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("id", player.getId());
        dto.put("name", player.getUserName());

        return dto;
    }



    private Map<String, Object> makeLeaderBoardDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Set<GamePlayer> gamePlayerSet = player.getGamePlayers();
        Set<Score> scores = player.getScores();

        List<Double> pointsList = scores.stream()
                .map(score -> score.getScore())
                .collect(toList());

        dto.put("id", player.getId());
        dto.put("name", player.getUserName());
        dto.put("totalScore", pointsList);

        return dto;
    }

    private Map<String, Object> authPlayerDTO (Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("id", currentPlayer(authentication).getId());
        dto.put("name", currentPlayer(authentication).getUserName());

        return dto;
    }


    private Player currentPlayer (Authentication authentication){
        return playerRepository.findByUserName(authentication.getName());
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }




    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGamesViewInfo (@PathVariable Long gamePlayerId, Authentication authentication){

        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
        Game game = gamePlayer.getGame();
        Set<Ship> ships = gamePlayer.getShips();
        GamePlayer oponent = oponentGamePlayer(gamePlayer);

        if (gamePlayer.getPlayer().getId() != currentPlayer(authentication).getId()) {
            return new ResponseEntity<>(makeMap("error", "This is not your game!")
                    , HttpStatus.FORBIDDEN);

        }else{
            Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("id", gamePlayerId);
            dto.put("game", makeGamesDTOGames_view(game, gamePlayer));
            dto.put("currentPlayer", authPlayerDTO(authentication));


            if(oponent == null) {
                dto.put("gameState", GameState.Waiting_For_Oponent);
            }else{
                Map<String, Integer> myFleet = mapObjectFleet();
                Map<String, Integer> oponFleet = mapObjectFleet();
                dto.put("hits", SetshipsCrossSetSalvos(gamePlayer, myFleet, oponFleet));
                dto.put("myFleet", myFleet);
                dto.put("oponFleet", oponFleet);
                dto.put("gameState", knowGameState(gamePlayer, myFleet, oponFleet));
//
            }

            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }


    private GameState getGameState (GamePlayer gamePlayer){
        GamePlayer oponent = oponentGamePlayer(gamePlayer);
        if(oponent == null){
            return GameState.Waiting_For_Oponent;
        }

        Map<String, Integer> myFleet = mapObjectFleet();
        Map<String, Integer> oponFleet = mapObjectFleet();
        SetshipsCrossSetSalvos(gamePlayer, myFleet, oponFleet);
        return knowGameState(gamePlayer, myFleet, oponFleet);
    }

    private GameState knowGameState (GamePlayer gamePlayer,
                                     Map<String, Integer> myFleet,
                                     Map<String, Integer> oponFleet){
//
        GamePlayer oponent = oponentGamePlayer(gamePlayer);
        if(oponent == null){
            return GameState.Waiting_For_Oponent;
        }


        GamePlayer creator;
        GamePlayer joined;
        if(gamePlayer.getId() < oponent.getId()){
            creator = gamePlayer;
            joined = oponent;

        }else{
            creator = oponent;
            joined = gamePlayer;
        }


        if(creator.getSalvos().size() == joined.getSalvos().size()
                && myFleet.get("fleetLeft") == 0 && oponFleet.get("fleetLeft") != 0){
            return GameState.GameOver_Lost;

        }else if(creator.getSalvos().size() == joined.getSalvos().size()
                && myFleet.get("fleetLeft") != 0 && oponFleet.get("fleetLeft") == 0){
            return GameState.GameOver_Won;

        }else if(creator.getSalvos().size() == joined.getSalvos().size()
                && myFleet.get("fleetLeft") == 0 && oponFleet.get("fleetLeft") == 0){
            return GameState.GameOver_Tied;
        }


        if(gamePlayer.getShips().size() != 5){
            return GameState.Placing_Ships;

        }else if(oponent.getShips().size() != 5){
            return GameState.Waiting_Ships_Oponent;

        }



        if(creator.getSalvos().size() == 0){
            if(creator == gamePlayer){
                return GameState.Firing_Salvos;

            }else if(creator == oponent){
                return GameState.Waiting_Salvos_Oponent;

            }
        }

        if(creator.getSalvos().size() > joined.getSalvos().size()){
            if(creator == gamePlayer){
                return GameState.Waiting_Salvos_Oponent;

            }else{
                return GameState.Firing_Salvos;
            }
        }

        if(creator.getSalvos().size() != 0 && joined.getSalvos().size() != 0
                && creator.getSalvos().size() == joined.getSalvos().size()){
            if(creator == gamePlayer){
                return GameState.Firing_Salvos;
            }else{
                return GameState.Waiting_Salvos_Oponent;
            }
        }

        return GameState.ERROR_TESTING;
    }



    private GamePlayer oponentGamePlayer (GamePlayer gamePlayer){
        Long gamePlayerID = gamePlayer.getId();
        Set<GamePlayer> meAndOponent = gamePlayer.getGame().getGamePlayers();

        List <GamePlayer> oponent = meAndOponent.stream()
//                                .filter(gP -> gP.getPlayer().getId() != gamePlayerID)
                                .filter(gP -> gP.getId() != gamePlayerID)
//                                .findFirst().orElse(null);
                                .collect(toList());
//                                  findFirst or Else null
        GamePlayer enemy = null;
        if(!oponent.isEmpty()){
            enemy = oponent.get(0);
        }
//
        return enemy;
//
    }

    private Map<String, Integer> mapObjectFleet(){
        Map<String, Integer> fleet = new HashMap<String, Integer>();
        fleet.put("patrolBoat", 2);
        fleet.put("destroyer", 3);
        fleet.put("submarine", 3);
        fleet.put("battleship", 4);
        fleet.put("aircraftCarrier", 5);

        fleet.put("fleetLeft", 5);

        return fleet;
    }


    private Map<String, Object> SetshipsCrossSetSalvos (GamePlayer gamePlayer, Map<String, Integer> myFleet, Map<String, Integer> oponFleet){
        Map<String, Object> dto = new HashMap<String, Object>();


        Set<Ship> myShips = gamePlayer.getShips();
        Set<Salvo> oponentsSalvos = oponentGamePlayer(gamePlayer).getSalvos();

        Set<Salvo> mySalvos = gamePlayer.getSalvos();
        Set<Ship> oponentShips = oponentGamePlayer(gamePlayer).getShips();


        dto.put("hitsIdo", mySalvos.stream()
                                    .sorted((s1, s2) -> s1.getTurn() - s2.getTurn())
                                    .map(salvo -> SetshipscrossSalvo(oponentShips, salvo, oponFleet))
                                    .collect(toList()));

        dto.put("hitsIRecieve", oponentsSalvos.stream()
                                                .sorted((s1, s2) -> s1.getTurn() - s2.getTurn())
                                                .map(salvo -> SetshipscrossSalvo(myShips, salvo, myFleet))
                                                .collect(toList()));

        return dto;
    }



    private Map<String, Object> SetshipscrossSalvo (Set<Ship> ships, Salvo salvo, Map<String, Integer> fleet){
        Map<String, Object> dto = new HashMap<String, Object>();

        List<Object> shipsSalvo = ships.stream()
                                    .filter(ship -> fleet.get(ship.getType()) != 0)
                                    .map(ship -> shipCrossSalvo(ship, salvo, fleet))
                                    .filter(shipCrossedSalvo -> !shipCrossedSalvo.isEmpty())
                                    .collect(toList());

        if(!shipsSalvo.isEmpty()){
            dto.put("turn", salvo.getTurn());
            dto.put("hit", shipsSalvo);

        }else{
            dto.put("hit", null);
        }

        return dto;
    }



    private Map<String, Object> shipCrossSalvo (Ship ship, Salvo salvo, Map<String, Integer> fleet){
        Map<String, Object> dto = new HashMap<String, Object>();

        List<String> salvoLocat = salvo.getSalvoLocation();

        List<String> shipLocat = ship.getLocation();
        List<String> shipsHit = shipLocat.stream()
                                    .filter(locat -> salvoLocat.contains(locat))
                                    .collect(toList());

        if(shipsHit.size() != 0){
            dto.put("type", ship.getType());
            dto.put("location", shipsHit);

            fleet.put(ship.getType(), fleet.get(ship.getType()) - shipsHit.size());

            if(fleet.get(ship.getType()) == 0){
                fleet.put("fleetLeft", fleet.get("fleetLeft") - 1);
            }
        }

        return dto;
    }


    private Map<String, Object> makeGamesDTOGames_view (Game game, GamePlayer gamePlayering){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Set<GamePlayer> gamePlayers = game.getGamePlayers();
        Set<Ship> ships = gamePlayering.getShips();
        Set<Salvo> salvos = gamePlayering.getSalvos();

        dto.put("id", game.getId());
        dto.put("create", game.getDate());
        dto.put("gamePlayers", gamePlayers.stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(toList()));
        dto.put("ship", ships.stream()
                .map(ship -> makeShipDTO(ship))
                .collect(toList()));
        dto.put("salvoes", gamePlayers.stream()
                .map(gp -> makeSalvoesDTO(gp))
                .collect(toList()));

        return dto;
    }


    private Map<String, Object> makeShipDTO(Ship ship){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocation());
        return dto;
    }


    private List<Object> makeSalvoesDTO(GamePlayer gamePlayer){
        List<Object> salvoList = new ArrayList<>();
        Set<Salvo> salvos = gamePlayer.getSalvos();

        salvoList = salvos.stream()
                .map(salvo -> makeSalvoDTO(salvo))
                .collect(toList());

        return salvoList;
    }

    private Map<String, Object> makeSalvoDTO(Salvo salvo){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
            dto.put("turn", salvo.getTurn());
            dto.put("gamePlayer_id", salvo.getGamePlayer().getId());
            dto.put("location", salvo.getSalvoLocation());

        return dto;
    }


    //------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String userName, @RequestParam String password) {
        if (userName.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(userName);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Name in use"),
                                                HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerRepository.save(new Player(userName, password));
        return new ResponseEntity<>(makeMap("name", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }


    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(Authentication authentication) {
        if(authentication == null){
            return new ResponseEntity<>(makeMap("error", "No logged in player to create game")
                                                , HttpStatus.UNAUTHORIZED);
        }else{
            Game game = gameRepo.save(new Game());
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, currentPlayer(authentication)));

            return new ResponseEntity<>(makeMap("gamePlayerCreated", gamePlayer.getId())
                                                , HttpStatus.CREATED);
        }
    }


    @RequestMapping(path = "/game/{IDgameToJoin}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getGameJoin (@PathVariable Long IDgameToJoin, Authentication authentication){
        Game game = gameRepo.findOne(IDgameToJoin);
        Player player = currentPlayer(authentication);

        if(authentication == null){
            return new ResponseEntity<>(makeMap("error", "Need to be logged in to join a game")
                    , HttpStatus.UNAUTHORIZED);
        }else if(game == null){
            return new ResponseEntity<>(makeMap("error", "No existing game")
                    , HttpStatus.FORBIDDEN);
        }else{
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, player));
            return new ResponseEntity<>(makeMap("gamePlayerID", gamePlayer.getId())
                    , HttpStatus.CREATED);
        }
    }


    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placingShips(@PathVariable long gamePlayerId,
                                                            @RequestBody Set<Ship> ships,
                                                            Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);

        if(authentication == null){
            return new ResponseEntity<>(makeMap("error", "Need to be logged in to placing Ships")
                    , HttpStatus.UNAUTHORIZED);

        }else if(gamePlayer == null){
            return new ResponseEntity<>(makeMap("error", "The gamePlayer does not exist")
                    , HttpStatus.UNAUTHORIZED);

        }else if(gamePlayer.getPlayer() != currentPlayer(authentication)){
            return new ResponseEntity<>(makeMap("error", "This is not your game (the current User is not in this gamePlayer)")
                    , HttpStatus.UNAUTHORIZED);

        }else if(gamePlayer.getShips().size() !=0) {
            return new ResponseEntity<>(makeMap("error", "Ships already has been placed")
                    , HttpStatus.FORBIDDEN);

        }else if(getGameState(gamePlayer) != GameState.Placing_Ships){
            return new ResponseEntity<>(makeMap("error", "Not allowed to place ships right now")
                    , HttpStatus.FORBIDDEN);

        }else{
            for (Ship ship : ships){
                ship.setGamePlayer(gamePlayer);
                shipRepository.save(ship);
            }
            return new ResponseEntity<>(makeMap("success", "Created and saved ships")
                    , HttpStatus.CREATED);
        }
    }


    @RequestMapping(path="/games/players/{gamePlayerId}/salvos", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placingSalvos(@PathVariable long gamePlayerId,
                                                             @RequestBody List<String> locationSalvos,
                                                             //NO SET DE SALVOES PERQUÈ HI HA DIFERENTS TORNS
                                                             Authentication authentication){

        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);

        if(authentication == null){
            return new ResponseEntity<>(makeMap("error", "Need to be logged in to fire salvos")
                                                , HttpStatus.UNAUTHORIZED);


        }else if(gamePlayer == null){
            return new ResponseEntity<>(makeMap("error", "The gamePlayer does not exist")
                                        , HttpStatus.UNAUTHORIZED);


        }else if(gamePlayer.getPlayer() != currentPlayer(authentication)) {
            return new ResponseEntity<>(makeMap("error", "This is not your game (the current User is not in this gamePlayer)")
                    , HttpStatus.FORBIDDEN);

        }else if (getGameState(gamePlayer) != GameState.Firing_Salvos){
            return new ResponseEntity<>(makeMap("error", "Not on the moment to Fire. GameState != Firing Salvos")
                    , HttpStatus.FORBIDDEN);

        }else{
            Salvo salvo = new Salvo();
            salvo.setSalvoLocation(locationSalvos);
            salvo.setGamePlayer(gamePlayer);
            salvo.setTurn(gamePlayer.getSalvos().size() + 1);

            if(salvo.getTurn() == gamePlayer.getSalvos().size()+1 || salvo.getTurn() == null){
                salvoRepository.save(salvo);
                gamePlayer.addSalvos(salvo);

                Player player = gamePlayer.getPlayer();
                Game game = gamePlayer.getGame();

                GamePlayer oponent = oponentGamePlayer(gamePlayer);
                Player oponentPlayer = oponent.getPlayer();

                if(getGameState(gamePlayer) == GameState.GameOver_Won){
                    Score myScoreWin = new Score(game, player, 1.0);
                    Score oponScoreLost = new Score(game, oponentPlayer, 0.0);
                    scoreRepository.save(myScoreWin);
                    scoreRepository.save(oponScoreLost);

                }else if(getGameState(gamePlayer) == GameState.GameOver_Lost){
                    Score myScoreLost = new Score(game, player, 0.0);
                    Score oponScoreWin = new Score(game, oponentPlayer, 1.0);
                    scoreRepository.save(myScoreLost);
                    scoreRepository.save(oponScoreWin);

                }else if(getGameState(gamePlayer) == GameState.GameOver_Tied){
                    Score myScoreTied = new Score(game, player, 0.5);
                    Score oponScoreTied = new Score (game, oponentPlayer, 0.5);
                    scoreRepository.save(myScoreTied);
                    scoreRepository.save(oponScoreTied);


                }

                return new ResponseEntity<>(makeMap("success", "Created and saved salvos")
                        , HttpStatus.CREATED);

            }else{
                return new ResponseEntity<>(makeMap("error", "You already shot this turn"),HttpStatus.FORBIDDEN);

            }
        }
    }
}