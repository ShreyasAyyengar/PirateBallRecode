package me.shreyasayyengar.pirateballremastered.game;

import me.shreyasayyengar.pirateballremastered.arena.Arena;
import me.shreyasayyengar.pirateballremastered.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class GamePlayer {

    private static final HashMap<UUID, GamePlayer> gamePlayerHashMap = new HashMap<>();

    private final UUID playerUUID;
    private final Team team;
    private final Arena currentArena;

    public GamePlayer(UUID playerUUID, Team team, Arena currentArena) {
        this.playerUUID = playerUUID;
        this.team = team;
        this.currentArena = currentArena;
//        for (Team currentTeam : currentArena.getAllTeams()) {
//            if (currentTeam.getDisplayName().equals(team.getDisplayName())) {
////                currentTeam.addPlayer();
//            }
//        }
        gamePlayerHashMap.put(playerUUID, this);
    }

    public static HashMap<UUID, GamePlayer> getGamePlayerHashMap() {
        return gamePlayerHashMap;
    }

    public static boolean isGamePlayer(UUID uuid) {
        return gamePlayerHashMap.containsKey(uuid);
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(this.playerUUID) != null /*&& Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).isOnline()*/;
    }

    public Team getTeamRegionIn() {
        Location location = this.toBukkitPlayer().getLocation();

        for (Team team : currentArena.getAllTeams()) {
            if (team.getRegion().isInRegion(location)) {
                return team;
            }
        }

        return null;
    }

    public void applyArmor() {

        ItemStack[] armor = {
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_HELMET)
        };
        LeatherArmorMeta teamArmor = (LeatherArmorMeta) new ItemStack(Material.LEATHER_HELMET).getItemMeta();

        teamArmor.setColor(team.getLeatherColour());
        teamArmor.setDisplayName(team.getDisplayName() + " Team");

        Arrays.stream(armor).forEach(itemStack -> itemStack.setItemMeta(teamArmor));

        this.toBukkitPlayer().getInventory().setArmorContents(armor);
    }

    // Getters ---------------------------------------------------------------------------------------------------------
    /**
     * Must only be called after verifying the player is not null
     * using {@link #isOnline()}
     */
    public Player toBukkitPlayer() {
        return Bukkit.getPlayer(this.playerUUID);
    }

    public Team getTeam() {
        return team;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Arena getCurrentArena() {
        return currentArena;
    }
}
