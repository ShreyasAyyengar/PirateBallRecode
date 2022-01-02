package me.shreyasayyengar.pirateballremastered.commands;

import me.shreyasayyengar.pirateballremastered.arena.Arena;
import me.shreyasayyengar.pirateballremastered.game.GameManager;
import me.shreyasayyengar.pirateballremastered.utils.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PirateBallBaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {

            if (args.length == 1) {
                if (args[0].equals("balls")) {
                    Utility.dropBalls(player);
                }

                if (args[0].equals("start")) {
                    new Arena().addPlayer(player);
                }

                if (args[0].equals("join")) {
                    GameManager.getArenas().get(0).addPlayer(player);
                }

            }
        }
        return false;
    }
}
