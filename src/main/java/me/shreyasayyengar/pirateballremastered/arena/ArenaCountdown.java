package me.shreyasayyengar.pirateballremastered.arena;

import me.shreyasayyengar.pirateballremastered.PirateBallPlugin;
import me.shreyasayyengar.pirateballremastered.game.GameState;
import me.shreyasayyengar.pirateballremastered.utils.Utility;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaCountdown extends BukkitRunnable {

    private final Arena arena;
    private int seconds;

    public ArenaCountdown(Arena arena) {
        this.arena = arena;
        this.seconds = 5;
    }

    public void begin() {
        this.runTaskTimer(PirateBallPlugin.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        if (seconds == 0) {
            cancel();
            arena.setGameState(GameState.LIVE);
            return;
        }

        if (seconds % 30 == 0 || seconds <= 10) {
            if (seconds == 1) {
                arena.broadcast(Utility.colourise("&eThe game will start in &c1 &esecond!"));
            } else {
                arena.broadcast(Utility.colourise("&eThe game will start in &c" + seconds + "&e seconds!"));
            }
        }

        if (arena.getBukkitPlayers().size() < Arena.REQUIRED_PLAYERS) {
            cancel();
            arena.setGameState(GameState.WAITING);
            arena.broadcast(Utility.colourise("&cToo little players, start cancelled"));
//            arena.sendTitle(ChatColor.RED + "Waiting for more players...", null, 0, 200, 20);
            return;
        }

        seconds--;
    }

}