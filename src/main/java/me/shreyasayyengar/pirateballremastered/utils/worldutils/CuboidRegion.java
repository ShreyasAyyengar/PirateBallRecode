package me.shreyasayyengar.pirateballremastered.utils.worldutils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CuboidRegion {

    private final String world;
    private final Vector minV, maxV;
    private final String name;

    public CuboidRegion(String name, Location min, Location max) {
        this.name = name;
        world = min.getWorld().getName();

        double x1= Math.min(min.getX(), max.getX());
        double y1= Math.min(min.getY(), max.getY());
        double z1= Math.min(min.getZ(), max.getZ());

        double x2= Math.max(min.getX(), max.getX());
        double y2= Math.max(min.getY(), max.getY());
        double z2= Math.max(min.getZ(), max.getZ());

        minV = new Vector(x1, y1, z1);
        maxV = new Vector(x2, y2, z2);
    }

    public boolean isInRegion(Location location) {
        return location.toVector().isInAABB(minV, maxV);
    }

//    public boolean isBlockInTeamZone(Block block, Team team) {
//        return team.getTeamZone(team).isInRegion(block.getLocation());
//    }

    public String getName() {
        return name;
    }
}
