package me.shreyasayyengar.pirateballremastered.arena;

import me.shreyasayyengar.pirateballremastered.exception.GamePlayerNotFoundException;
import me.shreyasayyengar.pirateballremastered.game.Game;
import me.shreyasayyengar.pirateballremastered.game.GameManager;
import me.shreyasayyengar.pirateballremastered.game.GamePlayer;
import me.shreyasayyengar.pirateballremastered.game.GameState;
import me.shreyasayyengar.pirateballremastered.teams.Team;
import me.shreyasayyengar.pirateballremastered.teams.TeamBall;
import me.shreyasayyengar.pirateballremastered.teams.TeamBallInfo;
import me.shreyasayyengar.pirateballremastered.teams.TeamInfo;
import me.shreyasayyengar.pirateballremastered.utils.ConfigManager;
import me.shreyasayyengar.pirateballremastered.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Arena {

    public static final int REQUIRED_PLAYERS = ConfigManager.getRequiredPlayers();

    private final ArrayList<GamePlayer> gamePlayers = new ArrayList<>();

    private final ArrayList<Team> allTeams = new ArrayList<>();
    private final Team redTeam = new Team(TeamInfo.RED, new TeamBall(TeamBallInfo.RED));
    private final Team blueTeam = new Team(TeamInfo.BLUE, new TeamBall(TeamBallInfo.BLUE));
    private final Team yellowTeam = new Team(TeamInfo.YELLOW, new TeamBall(TeamBallInfo.YELLOW));
    private final Team greenTeam = new Team(TeamInfo.GREEN, new TeamBall(TeamBallInfo.GREEN));

    private Game gameInstance;
    private GameState gameState;

    public Arena() {
        GameManager.getArenas().add(this);
        registerTeams();
        createGameInstance();
    }

    /**
     * Creates a new {@link Game} that corresponds with a live instance of {@link Arena}.
     */
    private void createGameInstance() {
        this.gameInstance = new Game(this);
        this.setGameState(GameState.WAITING);
    }

    /**
     * Adds all four teams (Red, Blue, Yellow, Green)
     * to the allTeams ArrayList.
     */
    private void registerTeams() {
        this.allTeams.add(redTeam);
        this.allTeams.add(blueTeam);
        this.allTeams.add(yellowTeam);
        this.allTeams.add(greenTeam);
    }

    /**
     * Adds a {@link Player} to a live instance of an {@link Arena}.
     *
     * @param player An online {@link Player}.
     */
    public void addPlayer(@NotNull Player player) {
        Team teamToAssign = getRandomTeam();
        GamePlayer gamePlayer = new GamePlayer(player.getUniqueId(), teamToAssign, this);

        gamePlayers.add(gamePlayer); // add player to total arena players
        teamToAssign.addPlayer(gamePlayer); // add player to actual team

    }

    /**
     * Removes a {@link GamePlayer} from a live instance of an {@link Arena}.
     * It is expected that all players in an arena have a matching {@link GamePlayer} object.
     * <p></p>
     *
     * @param gamePlayer A {@link GamePlayer}.
     */
    public void removePlayer(GamePlayer gamePlayer) {
        Team team = gamePlayer.getTeam();
        team.removePlayer(gamePlayer);
        gamePlayers.remove(gamePlayer);
    }

    /**
     * Broadcasts a string to all online {@link GamePlayer}s in an arena.
     *
     * @param message The string message.
     */
    public void broadcast(String message) {
        for (GamePlayer gamePlayer : this.gamePlayers) {
            if (gamePlayer.isOnline()) {
                gamePlayer.toBukkitPlayer().sendMessage(Utility.colourise(message));
            }
        }
    }

    /**
     * Gets a random {@link Team} using a random integer
     * which is used to get the index of the allTeams ArrayList.
     *
     * @return The random {@link Team}.
     */
    public Team getRandomTeam() {
        int random = new Random().nextInt(4);
        return this.allTeams.get(random);
    }

    /**
     * Initiates a countdown for a live instance of the {@link Arena}.
     */
    private void startCountdown() {
        new ArenaCountdown(this).begin();
    }


    // Getters & Setters -----------------------------------------------------------------------------

    public ArrayList<Player> getBukkitPlayers() {
        ArrayList<Player> players = new ArrayList<>();

        for (GamePlayer player : gamePlayers) {
            if (Bukkit.getPlayer(player.getPlayerUUID()) != null) {
                players.add(Bukkit.getPlayer(player.getPlayerUUID()));
            }
        }

        return players;
    }

    public ArrayList<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Game getGameInstance() {
        return gameInstance;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;

        switch (gameState) {
            case WAITING -> gameInstance.shouldStartCountdown();

            case COUNTDOWN -> startCountdown();

            case LIVE -> gameInstance.startGame();

            case WON -> {
                // todo announce winner
            }
        }
    }

    public ArrayList<Team> getAllTeams() {
        return allTeams;
    }

    /**
     * Gets an instance of {@link GamePlayer} from a UUID.
     * <p></p>
     *
     * @param uniqueId The UUID.
     * @return {@link GamePlayer} The matching {@link GamePlayer}.
     * @throws GamePlayerNotFoundException If no matching {@link GamePlayer} was found from the UUID provided.
     */
    public GamePlayer gamePlayerFromUUID(UUID uniqueId) throws GamePlayerNotFoundException {

        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getPlayerUUID().equals(uniqueId)) {
                return gamePlayer;
            }
        }
        throw new GamePlayerNotFoundException("GamePlayer with UUID: " + uniqueId + " not found!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return Objects.equals(gamePlayers, arena.gamePlayers) && Objects.equals(allTeams, arena.allTeams) && Objects.equals(redTeam, arena.redTeam) && Objects.equals(blueTeam, arena.blueTeam) && Objects.equals(yellowTeam, arena.yellowTeam) && Objects.equals(greenTeam, arena.greenTeam) && Objects.equals(gameInstance, arena.gameInstance) && gameState == arena.gameState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gamePlayers, allTeams, redTeam, blueTeam, yellowTeam, greenTeam, gameInstance, gameState);
    }
}
