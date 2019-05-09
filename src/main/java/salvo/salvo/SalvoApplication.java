package salvo.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository,
									  SalvoRepository salvoRepository,
									  ScoreRepository scoreRepository) {
		return (args) -> {

			Player bauer = new Player("j.bauer@ctu.gov", "24");
			Player brian = new Player("c.obrian@ctu.gov", "42");
			Player kim = new Player("kim_bauer@gmail.com", "kb");
			Player almeida = new Player("t.almeida@ctu.gov", "mole");

			playerRepository.save(bauer);
			playerRepository.save(brian);
			playerRepository.save(kim);
			playerRepository.save(almeida);


			Game game1 = new Game();
			Game game2 = new Game();
			Game game3 = new Game();
			Date game1Date = game1.getDate();
			Date game2Date = (Date.from(game1Date.toInstant().plusSeconds(3600)));
			Date game3Date = (Date.from(game1Date.toInstant().plusSeconds(7200)));
			game2.setDate(game2Date);
			game3.setDate(game3Date);

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);



			Game game4 = new Game();
			Game game5 = new Game();
			Game game6 = new Game();
			Game game7 = new Game();
			Game game8 = new Game();

			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);

			GamePlayer gamePlayer_1_1 = new GamePlayer(game1, bauer);
			GamePlayer gamePlayer_1_2 = new GamePlayer(game1, brian);
			GamePlayer gamePlayer_2_1 = new GamePlayer(game2, bauer);
			GamePlayer gamePlayer_2_2 = new GamePlayer(game2, brian);
			GamePlayer gamePlayer_3_1 = new GamePlayer(game3, brian);
			GamePlayer gamePlayer_3_2 = new GamePlayer(game3, almeida);
			GamePlayer gamePlayer_4_1 = new GamePlayer(game4, brian);
			GamePlayer gamePlayer_4_2 = new GamePlayer(game4, bauer);
			GamePlayer gamePlayer_5_1 = new GamePlayer(game5, almeida);
			GamePlayer gamePlayer_5_2 = new GamePlayer(game5, bauer);
			GamePlayer gamePlayer_6_1 = new GamePlayer(game6, kim);
			GamePlayer gamePlayer_7_1 = new GamePlayer(game7, almeida);
			GamePlayer gamePlayer_8_1 = new GamePlayer(game8, kim);
			GamePlayer gamePlayer_8_2 = new GamePlayer(game8, almeida);

			gamePlayerRepository.save(gamePlayer_1_1);
			gamePlayerRepository.save(gamePlayer_1_2);
			gamePlayerRepository.save(gamePlayer_2_1);
			gamePlayerRepository.save(gamePlayer_2_2);
			gamePlayerRepository.save(gamePlayer_3_1);
			gamePlayerRepository.save(gamePlayer_3_2);
			gamePlayerRepository.save(gamePlayer_4_1);
			gamePlayerRepository.save(gamePlayer_4_2);
			gamePlayerRepository.save(gamePlayer_5_1);
			gamePlayerRepository.save(gamePlayer_5_2);
			gamePlayerRepository.save(gamePlayer_6_1);
			gamePlayerRepository.save(gamePlayer_7_1);
			gamePlayerRepository.save(gamePlayer_8_1);
			gamePlayerRepository.save(gamePlayer_8_2);



			//NOMENCLATURA:
			//loc = location
			//loc1 = location del game1
			//locl_1= location del game1 del jugador 1
			//loc1_2 = location del game1 del jugador 2
			//loca1_1/2_1/2/3 = location del game1, del jugador1/jugador2
			//ship 1 o 2 o 3. Ex: loc1_2_2 = game1, jugador 2, ship 2
			List<String> loc1_1_1 = Arrays.asList("H2", "H3", "H4");
			List<String> loc1_1_2 = Arrays.asList("E1", "F1", "G1");
			List<String> loc1_1_3 = Arrays.asList("B4", "B5");
			List<String> loc1_2_1 = Arrays.asList("B5", "C5", "D5");
			List<String> loc1_2_2 = Arrays.asList("F1", "F2");

			List<String> loc2_1_1 = Arrays.asList("B5", "C5", "D5");
			List<String> loc2_1_2 = Arrays.asList("C6", "C7");
			List<String> loc2_2_1 = Arrays.asList("A2", "A3", "A4");
			List<String> loc2_2_2 = Arrays.asList("G6", "H6");

			List<String> loc3_1_1 = Arrays.asList("B5", "C5", "D5");
			List<String> loc3_1_2 = Arrays.asList("C6", "C7");
			List<String> loc3_2_1 = Arrays.asList("A2", "A3", "A4");
			List<String> loc3_2_2 = Arrays.asList("G6", "H6");

			List<String> loc4_1_1 = Arrays.asList("B5", "C5", "D5");
			List<String> loc4_1_2 = Arrays.asList("C6", "C7");
			List<String> loc4_2_1 = Arrays.asList("A2", "A3", "A4");
			List<String> loc4_2_2 = Arrays.asList("G6", "H6");

			List<String> loc5_1_1 = Arrays.asList("B5", "C5", "D5");
			List<String> loc5_1_2 = Arrays.asList("C6", "C7");
			List<String> loc5_2_1 = Arrays.asList("A2", "A3", "A4");
			List<String> loc5_2_2 = Arrays.asList("G6", "H6");

			List<String> loc6_1_1 = Arrays.asList("B5", "C5", "D5");
			List<String> loc6_1_2 = Arrays.asList("C6", "C7");

			List<String> loc8_1_1 = Arrays.asList("B5", "C5", "D5");
			List<String> loc8_1_2 = Arrays.asList("C6", "C7");
			List<String> loc8_2_1 = Arrays.asList("A2", "A3", "A4");
			List<String> loc8_2_2 = Arrays.asList("G6", "H6");


			//NOMENCLATURA: tipusDeShip+n.Game+_n.Player
			//Ex: destroyer1_1 = destroyer del Game 1 del player 1
			//Ex: loc1_2_2 = ship del game 1, jugador 2, ship 2
			//Ex: gamePlayer_1_2 = gamePlayer del Game 1, Player 2
			Ship destroyer1_1 = new Ship("destroyer", loc1_1_1, gamePlayer_1_1);
			Ship submarine1_1 = new Ship("submarine", loc1_1_2, gamePlayer_1_1);
			Ship patrolBoat1_1 = new Ship("patrolBoat", loc1_1_3, gamePlayer_1_1);
			Ship destroyer1_2 = new Ship("destroyer", loc1_2_1, gamePlayer_1_2);
			Ship patrolBoat1_2 = new Ship("patrolBoat", loc1_2_2, gamePlayer_1_2);

			Ship destroyer2_1 = new Ship("destroyer", loc2_1_1, gamePlayer_2_1);
			Ship patrolBoat2_1 = new Ship("patrolBoat", loc2_1_2, gamePlayer_2_1);
			Ship submarine2_2 = new Ship("submarine", loc2_2_1, gamePlayer_2_2);
			Ship patrolBoat2_2 = new Ship("patrolBoat", loc2_2_2, gamePlayer_2_2);

			Ship destroyer3_1 = new Ship("destroyer", loc3_1_1, gamePlayer_3_1);
			Ship patrolBoat3_1 = new Ship("patrolBoat", loc3_1_2, gamePlayer_3_1);
			Ship submarine3_2 = new Ship("submarine", loc3_2_1, gamePlayer_3_2);
			Ship patrolBoat3_2 = new Ship("patrolBoat", loc3_2_2, gamePlayer_3_2);

			Ship destroyer4_1 = new Ship("destroyer", loc4_1_1, gamePlayer_4_1);
			Ship patrolBoat4_1 = new Ship("patrolBoat", loc4_1_2, gamePlayer_4_1);
			Ship submarine4_2 = new Ship("submarine", loc4_2_1, gamePlayer_4_2);
			Ship patrolBoat4_2 = new Ship("patrolBoat", loc4_2_2, gamePlayer_4_2);

			Ship destroyer5_1 = new Ship("destroyer", loc5_1_1, gamePlayer_5_1);
			Ship patrolBoat5_1 = new Ship("patrolBoat", loc5_1_2, gamePlayer_5_1);
			Ship submarine5_2 = new Ship("submarine", loc5_2_1, gamePlayer_5_2);
			Ship patrolBoat5_2 = new Ship("patrolBoat", loc5_2_2, gamePlayer_5_2);

			Ship destroyer6_1 = new Ship("destroyer", loc6_1_1, gamePlayer_6_1);
			Ship patrolBoat6_1 = new Ship("patrolBoat", loc6_1_2, gamePlayer_6_1);

			Ship destroyer8_1 = new Ship("destroyer", loc8_1_1, gamePlayer_8_1);
			Ship patrolBoat8_1 = new Ship("patrolBoat", loc8_1_2, gamePlayer_8_1);
			Ship submarine8_2 = new Ship("submarine", loc8_2_1, gamePlayer_8_2);
			Ship patrolBoat8_2 = new Ship("patrolBoat", loc8_2_2, gamePlayer_8_2);


			shipRepository.save(destroyer1_1);
			shipRepository.save(submarine1_1);
			shipRepository.save(patrolBoat1_1);
			shipRepository.save(destroyer1_2);
			shipRepository.save(patrolBoat1_2);

			shipRepository.save(destroyer2_1);
			shipRepository.save(patrolBoat2_1);
			shipRepository.save(submarine2_2);
			shipRepository.save(patrolBoat2_2);

			shipRepository.save(destroyer3_1);
			shipRepository.save(patrolBoat3_1);
			shipRepository.save(submarine3_2 );
			shipRepository.save(patrolBoat3_2);

			shipRepository.save(destroyer4_1);
			shipRepository.save(patrolBoat4_1);
			shipRepository.save(submarine4_2);
			shipRepository.save(patrolBoat4_2);

			shipRepository.save(destroyer5_1);
			shipRepository.save(patrolBoat5_1);
			shipRepository.save(submarine5_2);
			shipRepository.save(patrolBoat5_2);

			shipRepository.save(destroyer6_1);
			shipRepository.save(patrolBoat6_1);

			shipRepository.save(destroyer8_1);
			shipRepository.save(patrolBoat8_1);
			shipRepository.save(submarine8_2);
			shipRepository.save(patrolBoat8_2);


			//NOMENCLATURA: salvoLoc -- per distingir dels loc que són les
			//locations dels ships.
			//salvoLocX_Y_Z = X -- game
			//Y -- jugador
			//Z -- número de torn.
			//Ex: salvoLoc2_1_2 = salvo del game 2, jugador 1, salvo 2
			List<String> salvoLoc1_1_1 = Arrays.asList("B5", "C5", "F1");
			List<String> salvoLoc1_2_1 = Arrays.asList("B4", "B5", "B6");
			List<String> salvoLoc1_1_2 = Arrays.asList("F2", "D5");
			List<String> salvoLoc1_2_2 = Arrays.asList("E1", "H3", "A2");

			List<String> salvoLoc2_1_1 = Arrays.asList("A2", "A4", "G6");
			List<String> salvoLoc2_2_1 = Arrays.asList("B5", "D5", "C7");
			List<String> salvoLoc2_1_2 = Arrays.asList("A3", "H6");
			List<String> salvoLoc2_2_2 = Arrays.asList("C5", "C6");

			List<String> salvoLoc3_1_1 = Arrays.asList("G6", "H6", "A4");
			List<String> salvoLoc3_2_1 = Arrays.asList("H1", "H2", "H3");
			List<String> salvoLoc3_1_2 = Arrays.asList("A2", "A3", "D8");
			List<String> salvoLoc3_2_2 = Arrays.asList("E1", "F2", "G3");

			List<String> salvoLoc4_1_1 = Arrays.asList("A3", "A4", "F7");
			List<String> salvoLoc4_2_1 = Arrays.asList("B5", "C6", "H1");
			List<String> salvoLoc4_1_2 = Arrays.asList("A2", "G6", "H6");
			List<String> salvoLoc4_2_2 = Arrays.asList("C5", "C7", "D5");

			List<String> salvoLoc5_1_1 = Arrays.asList("A1", "A2", "A3");
			List<String> salvoLoc5_2_1 = Arrays.asList("B5", "B6", "C7");
			List<String> salvoLoc5_1_2 = Arrays.asList("G6", "G7", "G8");
			List<String> salvoLoc5_2_2 = Arrays.asList("C6", "D6", "E6");
			List<String> salvoLoc5_2_3 = Arrays.asList("H1", "H8");


			//NOMENCLATURA:
			//salvoX_Y_Z = X -- game, Y jugador, Z torn dispar salvo.
			//Ex: salvo1_1_2 = del game1, jugador 1, torn de dispar salvo 2.

			Salvo salvo1_1_1= new Salvo(gamePlayer_1_1, 1, salvoLoc1_1_1);
			Salvo salvo1_1_2 = new Salvo(gamePlayer_1_1, 2, salvoLoc1_1_2);
			Salvo salvo1_2_1 = new Salvo(gamePlayer_1_2, 1, salvoLoc1_2_1);
			Salvo salvo1_2_2 = new Salvo(gamePlayer_1_2, 2, salvoLoc1_2_2);

			Salvo salvo2_1_1 = new Salvo(gamePlayer_2_1, 1, salvoLoc2_1_1);
			Salvo salvo2_1_2 = new Salvo(gamePlayer_2_1, 2, salvoLoc2_1_2);
			Salvo salvo2_2_1 = new Salvo(gamePlayer_2_2, 1, salvoLoc2_2_1);
			Salvo salvo2_2_2 = new Salvo(gamePlayer_2_2, 2, salvoLoc2_2_2);

			Salvo salvo3_1_1 = new Salvo(gamePlayer_3_1, 1, salvoLoc3_1_1);
			Salvo salvo3_1_2 = new Salvo(gamePlayer_3_1, 2, salvoLoc3_1_2);
			Salvo salvo3_2_1 = new Salvo(gamePlayer_3_2, 1, salvoLoc3_2_1);
			Salvo salvo3_2_2 = new Salvo(gamePlayer_3_2, 2, salvoLoc3_2_2);


			Salvo salvo4_1_1 = new Salvo(gamePlayer_4_1, 1, salvoLoc4_1_1);
			Salvo salvo4_1_2 = new Salvo(gamePlayer_4_1, 2, salvoLoc4_1_2);
			Salvo salvo4_2_1 = new Salvo(gamePlayer_4_2, 1, salvoLoc4_2_1);
			Salvo salvo4_2_2 = new Salvo(gamePlayer_4_2, 2, salvoLoc4_2_2);


			Salvo salvo5_1_1 = new Salvo(gamePlayer_5_1, 1, salvoLoc5_1_1);
			Salvo salvo5_1_2 = new Salvo(gamePlayer_5_1, 2, salvoLoc5_1_2);
			Salvo salvo5_2_1 = new Salvo(gamePlayer_5_2, 1, salvoLoc5_2_1);
			Salvo salvo5_2_2 = new Salvo(gamePlayer_5_2, 2, salvoLoc5_2_2);
			Salvo salvo5_2_3 = new Salvo(gamePlayer_5_2, 3, salvoLoc5_2_3);


			salvoRepository.save(salvo1_1_1);
			salvoRepository.save(salvo1_1_2);
			salvoRepository.save(salvo1_2_1);
			salvoRepository.save(salvo1_2_2);
			salvoRepository.save(salvo2_1_1);
			salvoRepository.save(salvo2_1_2);
			salvoRepository.save(salvo2_2_1);
			salvoRepository.save(salvo2_2_2);
			salvoRepository.save(salvo3_1_1);
			salvoRepository.save(salvo3_1_2);
			salvoRepository.save(salvo3_2_1);
			salvoRepository.save(salvo3_2_2);
			salvoRepository.save(salvo4_1_1);
			salvoRepository.save(salvo4_1_2);
			salvoRepository.save(salvo4_2_1);
			salvoRepository.save(salvo4_2_2);
			salvoRepository.save(salvo5_1_1);
			salvoRepository.save(salvo5_1_2);
			salvoRepository.save(salvo5_2_1);
			salvoRepository.save(salvo5_2_2);
			salvoRepository.save(salvo5_2_3);



			//NOMENCLATURA: scoreX_Y = X: game; Y: jugador
			//score2_1 = score del game 2, jugador 1.
			Score score1_1 = new Score(game1, bauer, 1.0);
			Score score1_2 = new Score(game1, brian, 0.0);

			Score score2_1 = new Score(game2, bauer, 0.5);
			Score score2_2 = new Score(game2, brian, 0.5);

			Score score3_1 = new Score(game3, brian, 1.0);
			Score score3_2 = new Score(game3, almeida, 0.0);

			Score score4_1 = new Score(game4, brian, 0.5);
			Score score4_2 = new Score(game4, bauer, 0.5);


			scoreRepository.save(score1_1);
			scoreRepository.save(score1_2);
			scoreRepository.save(score2_1);
			scoreRepository.save(score2_2);
			scoreRepository.save(score3_1);
			scoreRepository.save(score3_2);
			scoreRepository.save(score4_1);
			scoreRepository.save(score4_2);


		};
	}

}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

  @Autowired
  PlayerRepository playerRepository;

  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(inputName-> {

      Player player = playerRepository.findByUserName(inputName);
        if (player != null) {
          return new User(player.getUserName(), player.getPassword(),
                  AuthorityUtils.createAuthorityList("USER"));
        } else {
          throw new UsernameNotFoundException("Unknown user: " + inputName);
        }
    });
  }
}


@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
					.antMatchers("/web/games.html").permitAll()
					.antMatchers("/web/scripts/games.js").permitAll()
					.antMatchers("/web/styles/games.css").permitAll()
					.antMatchers("/web/styles/images/**").permitAll()
					.antMatchers("/api/games").permitAll()
					.antMatchers("/api/players").permitAll()
					.antMatchers("/**").hasAuthority("USER")
	                .and()
	              .formLogin()
						.usernameParameter("userName")
			  			.passwordParameter("password")
			  			.loginPage("/api/login");

	  http.logout().logoutUrl("/api/logout");

	  // turn off checking for CSRF tokens
	  http.csrf().disable();

	  // if user is not authenticated, just send an authentication failure response
	  http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

	  // if login is successful, just clear the flags asking for authentication
	  http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

	  // if login fails, just send an authentication failure response
	  http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

	  // if logout is successful, just send a success response
	  http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
  }

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
  }



