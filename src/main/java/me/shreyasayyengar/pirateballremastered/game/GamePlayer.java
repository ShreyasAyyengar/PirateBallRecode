package me.shreyasayyengar.pirateballremastered.game;

import me.shreyasayyengar.pirateballremastered.teams.Team;

import java.util.HashMap;
import java.util.UUID;

public class GamePlayer {

    private final HashMap<UUID, GamePlayer> gamePlayerHashMap = new HashMap<>();

    private final UUID playerUUID;
    private final Team team;

    public GamePlayer(UUID playerUUID, Team team) {
        this.playerUUID = playerUUID;
        this.team = team;

        gamePlayerHashMap.put(playerUUID, this);
    }

    public Team getTeam() {
        return team;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }


}
