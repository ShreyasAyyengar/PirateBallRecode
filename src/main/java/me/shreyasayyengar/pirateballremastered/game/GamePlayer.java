package me.shreyasayyengar.pirateballremastered.game;

import me.shreyasayyengar.pirateballremastered.PirateBallPlugin;
import me.shreyasayyengar.pirateballremastered.arena.Arena;
import me.shreyasayyengar.pirateballremastered.teams.Team;
import me.shreyasayyengar.pirateballremastered.utils.ConfigManager;
import me.shreyasayyengar.pirateballremastered.utils.Utility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GamePlayer {

    private static final Map<UUID, GamePlayer> gamePlayerHashMap = new HashMap<>();

    private final UUID playerUUID;
    private final Team team;
    private final Arena currentArena;

    private Team lastStandingIn;

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

    public static Map<UUID, GamePlayer> getGamePlayerHashMap() {
        return gamePlayerHashMap;
    }

    public static boolean isGamePlayer(UUID uuid) {
        return gamePlayerHashMap.containsKey(uuid);
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(this.playerUUID) != null /*&& Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).isOnline()*/;
    }

    // Game methods ----------------------------------------------------------------------------------------------------
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

    public void applyDeathTitle(Game.GameMechanicsUtil.RespawnReason reason) {

        Player bukkitPlayer = this.toBukkitPlayer();

        String title = null;
        String subtitle = ChatColor.GRAY + "Moving to jail in ";

        switch (reason) {
            case KILLED -> title = "&cYou died to " + Objects.requireNonNull(this.toBukkitPlayer().getLastDamageCause()).getEntity().getName();

            case HIT_BY_BALL -> title = "&cYou've been hit by a ball!";

            case REMOVED_FROM_JAIL -> {
                title = "&aYou teammate has saved you!";
                subtitle = "&7Returning to base in ";
            }

            case RECONNECTED_SAFE -> {
                title = "&6Reconnected to the game!";
                subtitle = "&7Returning to base in ";
            }

            case RECONNECTED_UNSAFE -> {
                title = "&6Reconnected to the game!";
                subtitle = "&7Moving to jail in";
            }

        } // Title Factory

        final int[] seconds = {5};

        String finalTitle = title;
        String finalSubtitle = subtitle;
        new BukkitRunnable() {
            @Override
            public void run() {

                if (seconds[0] == 0) {
                    bukkitPlayer.sendMessage(Utility.colourise("&aRespawned"));

                    cancel();

                } else if (seconds[0] == 1) {
                    bukkitPlayer.sendTitle(Utility.colourise(finalTitle), Utility.colourise(finalSubtitle + "&e" + seconds[0] + " &7second"), 0, 25, 0);
                } else {
                    bukkitPlayer.sendTitle(Utility.colourise(finalTitle), Utility.colourise(finalSubtitle + "&e" + seconds[0] + " &7seconds"), 0, 25, 0);
                }
                seconds[0]--;
            }
        }.runTaskTimer(PirateBallPlugin.getInstance(), 0, 20);
    }

    public void setSpawningAction(Location toSpawn) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : GamePlayer.this.getCurrentArena().getBukkitPlayers()) {
                    player.showPlayer(PirateBallPlugin.getInstance(), GamePlayer.this.toBukkitPlayer());
                }

                GamePlayer.this.applyArmor();
                GamePlayer.this.toBukkitPlayer().setGameMode(GameMode.SURVIVAL);
                GamePlayer.this.toBukkitPlayer().getActivePotionEffects().clear();

                GamePlayer.this.toBukkitPlayer().teleport(toSpawn);
            }
        }.runTaskLater(PirateBallPlugin.getInstance(), 100L);
    }

    public void setDespawningAction() {
        for (Player player : this.getCurrentArena().getBukkitPlayers()) {
            player.hidePlayer(PirateBallPlugin.getInstance(), this.toBukkitPlayer());
        }

        this.toBukkitPlayer().getInventory().clear();
        this.toBukkitPlayer().setGameMode(GameMode.SPECTATOR);
        this.toBukkitPlayer().getActivePotionEffects().clear();
        this.toBukkitPlayer().teleport(ConfigManager.getSpectatorLocation());
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

    public Team getLastStandingIn() {
        return lastStandingIn;
    }

    public void setLastStandingIn(Team lastStandingIn) {
        this.lastStandingIn = lastStandingIn;
    }
}
