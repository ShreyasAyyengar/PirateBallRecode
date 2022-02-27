package me.shreyasayyengar.pirateballremastered.utils;

import me.shreyasayyengar.pirateballremastered.PirateBallPlugin;
import org.bukkit.Location;

public class ConfigManager {

    private static PirateBallPlugin main;

    public static void init(PirateBallPlugin main) {
        ConfigManager.main = main;
        main.getConfig().options().configuration();
        main.saveDefaultConfig();
    }

    public static int getRequiredPlayers() {
        return main.getConfig().getInt("required-players");
    }

    public static void setRequiredPlayers(int number) {
        main.getConfig().set("required-players", number);
        main.saveConfig();
    }

    public static void setSpectatorLocation(Location location) {
        main.getConfig().set("spectator-location", location);
        main.saveConfig();
    }

    public static Location getSpectatorLocation() {
       return main.getConfig().getLocation("spectator-location");
    }

}
