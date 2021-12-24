package me.shreyasayyengar.pirateballremastered.game;

import me.shreyasayyengar.pirateballremastered.teams.Team;
import me.shreyasayyengar.pirateballremastered.teams.TeamBall;
import me.shreyasayyengar.pirateballremastered.teams.TeamBallInfo;
import me.shreyasayyengar.pirateballremastered.teams.TeamInfo;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class Arena {

    private final ArrayList<GamePlayer> players = new ArrayList<>();
    private final ArrayList<Team> allTeams = new ArrayList<>();

    private final Team redTeam = new Team(TeamInfo.RED, new TeamBall(TeamBallInfo.RED));
    private final Team blueTeam = new Team(TeamInfo.BLUE, new TeamBall(TeamBallInfo.BLUE));
    private final Team yellowTeam = new Team(TeamInfo.YELLOW, new TeamBall(TeamBallInfo.YELLOW));
    private final Team greenTeam = new Team(TeamInfo.GREEN, new TeamBall(TeamBallInfo.GREEN));

    private GameState gameState = GameState.WAITING;

    public Arena() {
        this.allTeams.add(redTeam);
        this.allTeams.add(blueTeam);
        this.allTeams.add(yellowTeam);
        this.allTeams.add(greenTeam);
    }

    public void addPlayer(@NotNull Player player) {
        players.add(new GamePlayer(player.getUniqueId(), getRandomTeam())); // add to blue just for testing
    }

    public void removePlayer(GamePlayer player) {
        players.remove(player);
    }

    public ArrayList<GamePlayer> getPlayers() {
        return players;
    }

    public Team getRandomTeam() {
        int random = new Random().nextInt(4);
        return this.allTeams.get(random);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;

        switch (gameState) {
            case WAITING -> {
                // todo check players, initate countdown if ready;
            }

            case COUNTDOWN -> {
                // todo: begin coundown, check each tick for removed player;
            }

            case LIVE -> {
                // todo teleport players, apply player data, set map. start da game
            }

            case WON -> {
                // todo annouce winner
            }

        }
    }





}
