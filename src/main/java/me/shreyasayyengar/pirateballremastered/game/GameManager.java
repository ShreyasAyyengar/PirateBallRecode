package me.shreyasayyengar.pirateballremastered.game;

import me.shreyasayyengar.pirateballremastered.arena.Arena;
import me.shreyasayyengar.pirateballremastered.exception.ArenaNotFoundException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class GameManager {

    private static final ArrayList<Arena> arenas = new ArrayList<>();

    public static boolean isPlaying(@NotNull Player player) {
        UUID uuid = player.getUniqueId();

        for (Arena arena : GameManager.arenas) {

            for (GamePlayer gamePlayer : arena.getGamePlayers()) {

                if (gamePlayer.getPlayerUUID().equals(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Arena getArena(Player player) throws ArenaNotFoundException {
        for (Arena arena : arenas) {
            for (GamePlayer gamePlayer : arena.getGamePlayers()) {
                if (gamePlayer.getPlayerUUID().equals(player.getUniqueId())) {
                    return arena;
                }
            }
        }

        throw new ArenaNotFoundException(player.getName() + " was not matched to an running Arena instance");
    }

    public static ArrayList<Arena> getArenas() {
        return arenas;
    }

}