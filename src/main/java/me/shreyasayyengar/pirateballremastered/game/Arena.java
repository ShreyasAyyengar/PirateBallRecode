package me.shreyasayyengar.pirateballremastered.game;

import me.shreyasayyengar.pirateballremastered.teams.Team;
import me.shreyasayyengar.pirateballremastered.teams.TeamBall;
import me.shreyasayyengar.pirateballremastered.teams.TeamBallInfo;
import me.shreyasayyengar.pirateballremastered.teams.TeamInfo;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Arena {

    private final ArrayList<GamePlayer> players = new ArrayList<>();

    private final Team redTeam = new Team(TeamInfo.RED, new TeamBall(TeamBallInfo.RED));
    private final Team blueTeam = new Team(TeamInfo.BLUE, new TeamBall(TeamBallInfo.BLUE));
    private final Team yellowTeam = new Team(TeamInfo.YELLOW, new TeamBall(TeamBallInfo.YELLOW));
    private final Team greenTeam = new Team(TeamInfo.GREEN, new TeamBall(TeamBallInfo.GREEN));

    public void addPlayer(Player player) {
        players.add(new GamePlayer(player.getUniqueId(), new Team(blueTeam.getTeamData(), blueTeam.getBallData()))); // add to blue just for testing
    }

    public void removePlayer(Player player) {
        // how tf to design this??????
    }
}
