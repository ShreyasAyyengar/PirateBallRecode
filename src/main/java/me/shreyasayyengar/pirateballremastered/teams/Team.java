package me.shreyasayyengar.pirateballremastered.teams;

import me.shreyasayyengar.pirateballremastered.game.GamePlayer;
import me.shreyasayyengar.pirateballremastered.utils.Utility;
import me.shreyasayyengar.pirateballremastered.utils.worldutils.CuboidRegion;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.UUID;

public class Team {

    private final TeamInfo teamData;
    private final TeamBall ballData;

    private final HashMap<UUID, GamePlayer> players = new HashMap<>(); // ? maybe

    public Team(TeamInfo teamInfo, TeamBall ballInfo) {
        this.teamData = teamInfo;
        this.ballData = ballInfo;
    }

    public void addPlayer(GamePlayer gamePlayer) {
        players.put(gamePlayer.getPlayerUUID(), gamePlayer);

        sendTeamMessage(gamePlayer.toBukkitPlayer().getName() + " added!");

    }

    public void removePlayer(GamePlayer gamePlayer) {
        UUID uuid = gamePlayer.getPlayerUUID();
        sendTeamMessage(gamePlayer.toBukkitPlayer().getName() + " removed!");

        this.players.remove(uuid);
    }

    public void sendTeamMessage(String message) {
        for (GamePlayer gamePlayer : players.values()) {
            if (gamePlayer.isOnline()) {
                gamePlayer.toBukkitPlayer().sendMessage(Utility.colourise(message));
            }
        }

    }

    public void sendTeamActionBar(String message) {
        for (GamePlayer gamePlayer : players.values()) {
            if (gamePlayer.isOnline()) {
                Utility.sendActionBar(gamePlayer.toBukkitPlayer(), message);
            }
        }
    }

    public void sendTeamTitle(String message, String subtitle, int fadeIn, int stay, int fadeOut) {

        for (GamePlayer gamePlayer : players.values()) {
            if (gamePlayer.isOnline()) {
                gamePlayer.toBukkitPlayer().sendTitle(Utility.colourise(message), Utility.colourise(subtitle), fadeIn, stay, fadeOut);
            }
        }
    }


    // Getters ----------------------------------------------------------------
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

    public TeamInfo getData() {
        return teamData;
    }

    public HashMap<UUID, GamePlayer> getPlayers() {
        return players;
    }
    // Getters ----------------------------------------------------------------


}
