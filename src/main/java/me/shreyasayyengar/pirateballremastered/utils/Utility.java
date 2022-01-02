package me.shreyasayyengar.pirateballremastered.utils;

import me.shreyasayyengar.pirateballremastered.teams.TeamBall;
import me.shreyasayyengar.pirateballremastered.teams.TeamBallInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utility {

    private Utility() {
    }

    public static String colourise(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void bsout(String message) {
        Bukkit.getLogger().info(message);
    }

    public static void dropBalls(Player player) {
        for (TeamBallInfo team : TeamBallInfo.values()) {
            player.getWorld().dropItem(player.getLocation(), new TeamBall(team).getBall());
        }

        player.sendMessage(colourise("&aDropped!"));
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Utility.colourise(message)));
    }
}