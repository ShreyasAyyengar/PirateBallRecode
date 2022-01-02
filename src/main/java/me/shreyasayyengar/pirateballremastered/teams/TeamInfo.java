package me.shreyasayyengar.pirateballremastered.teams;

import me.shreyasayyengar.pirateballremastered.utils.Utility;
import me.shreyasayyengar.pirateballremastered.utils.worldutils.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public enum TeamInfo {

    RED(
            Utility.colourise("&cRed"),
            "&c",
            Color.RED,
            Material.RED_BANNER,
            Material.RED_WOOL,
            Material.RED_STAINED_GLASS,
            new CuboidRegion("RED", new Location(Bukkit.getWorld("world"), 58, 16, 58), new Location(Bukkit.getWorld("world"), 9, 4, 9)),
            new Location(Bukkit.getWorld("world"), 9, 4, 9),
            new Location(Bukkit.getWorld("world"), 9, 4, 9)

    ),

    BLUE(
            Utility.colourise("&9Blue"),
            "&9",
            Color.BLUE,
            Material.BLUE_BANNER,
            Material.BLUE_WOOL,
            Material.BLUE_STAINED_GLASS,
            new CuboidRegion("BLUE", new Location(Bukkit.getWorld("world"), 58, 16, -41), new Location(Bukkit.getWorld("world"), 9, 4, 7)),
            new Location(Bukkit.getWorld("world"), 9, 4, 7),
            new Location(Bukkit.getWorld("world"), 9, 4, 7)
    ),

    YELLOW(
            Utility.colourise("&eYellow"),
            "&e",
            Color.YELLOW,
            Material.YELLOW_BANNER,
            Material.YELLOW_WOOL,
            Material.YELLOW_STAINED_GLASS,
            new CuboidRegion("GREEN", new Location(Bukkit.getWorld("world"), -41, 16, 58), new Location(Bukkit.getWorld("world"), 7, 4, 9)),
            new Location(Bukkit.getWorld("world"), 7, 4, 9),
            new Location(Bukkit.getWorld("world"), 7, 4, 9)
    ),

    GREEN(
            Utility.colourise("&2Green"),
            "&2",
            Color.LIME,
            Material.LIME_BANNER,
            Material.LIME_WOOL,
            Material.LIME_STAINED_GLASS,
            new CuboidRegion("GREEN", new Location(Bukkit.getWorld("world"), -41, 16, -41), new Location(Bukkit.getWorld("world"), 7, 4, 7)),
            new Location(Bukkit.getWorld("world"), 7, 4, 7),
            new Location(Bukkit.getWorld("world"), 7, 4, 7)
    );

    private final String displayName;
    private final String chatString;
    private final Color leatherColour;
    private final Material banner;
    private final Material wool;
    private final Material glass;
    private final CuboidRegion region;
    private final Location spawn;
    private final Location jail;

    TeamInfo(String displayName, String chatString, Color leatherColour, Material banner, Material wool, Material glass, CuboidRegion region, Location spawn, Location jail) {
        this.displayName = displayName;
        this.chatString = chatString;
        this.leatherColour = leatherColour;
        this.banner = banner;
        this.wool = wool;
        this.glass = glass;
        this.region = region;
        this.spawn = spawn;
        this.jail = jail;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChatString() {
        return chatString;
    }

    public Color getLeatherColour() {
        return leatherColour;
    }

    public Material getBanner() {
        return banner;
    }

    public Material getWool() {
        return wool;
    }

    public Material getGlass() {
        return glass;
    }

    public CuboidRegion getRegion() {
        return region;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getJail() {
        return jail;
    }
}
