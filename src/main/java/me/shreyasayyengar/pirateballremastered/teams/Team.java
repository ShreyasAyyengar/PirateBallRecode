package me.shreyasayyengar.pirateballremastered.teams;

import me.shreyasayyengar.pirateballremastered.game.GamePlayer;
import me.shreyasayyengar.pirateballremastered.utils.worldutils.CuboidRegion;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.UUID;

public class Team {

    private final TeamInfo teamData;
    private final TeamBall ballData;

    //    private final List<UUID> players; will work out a custom player object for the game

    private final HashMap<UUID, GamePlayer> players = new HashMap<>(); // ? maybe

    public Team(TeamInfo teamInfo, TeamBall ballInfo) {
        this.teamData = teamInfo;
        this.ballData = ballInfo;

//        this.players = players; will work out a custom player object for the game

    }

    public String getDisplayName() {
        return teamData.getDisplayName();
    }

    public String getChatColorString() {
        return teamData.getChatString();
    }

    public Color getLeatherColour() {
        return teamData.getLeatherColour();
    }

    public Material getBanner() {
        return teamData.getBanner();
    }

    public Material getWool() {
        return teamData.getWool();
    }

    public Material getGlass() {
        return teamData.getGlass();
    }

    public CuboidRegion getRegion() {
        return teamData.getRegion();
    }

    public TeamBall getBallData() {
        return ballData;
    }

    public TeamInfo getTeamData() {
        return teamData;
    }


}
