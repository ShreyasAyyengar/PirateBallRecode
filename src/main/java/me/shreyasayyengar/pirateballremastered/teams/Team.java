package me.shreyasayyengar.pirateballremastered.teams;

import me.shreyasayyengar.pirateballremastered.game.GamePlayer;
import me.shreyasayyengar.pirateballremastered.utils.worldutils.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Team {

    private final TeamInfo teamData;
    private final TeamBall ballData;

    private final HashMap<UUID, GamePlayer> players = new HashMap<>(); // ? maybe

    public Team(TeamInfo teamInfo, TeamBall ballInfo) {
        this.teamData = teamInfo;
        this.ballData = ballInfo;

//        this.players = players; will work out a custom player object for the game

    }

    public void addPlayer(Player player, Team team) {
        players.put(player.getUniqueId(), new GamePlayer(player.getUniqueId(), team));

        sendTeamMessage(player.getName() + " added!");
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();

        sendTeamMessage(player.getName() + " removed!");

        this.players.remove(uuid);
    }

    private void sendTeamMessage(String message) {
        for (UUID playerUUID : players.keySet()) {

            Player player = Bukkit.getPlayer(playerUUID);

            if (Bukkit.getPlayer(playerUUID).isOnline()) {
                assert player != null;
                player.sendMessage(message);
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

    public TeamInfo getTeamData() {
        return teamData;
    }
    // Getters ----------------------------------------------------------------


}
