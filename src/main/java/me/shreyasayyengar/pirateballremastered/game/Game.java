package me.shreyasayyengar.pirateballremastered.game;

import me.shreyasayyengar.pirateballremastered.PirateBallPlugin;
import me.shreyasayyengar.pirateballremastered.arena.Arena;
import me.shreyasayyengar.pirateballremastered.exception.ArenaNotFoundException;
import me.shreyasayyengar.pirateballremastered.exception.GamePlayerNotFoundException;
import me.shreyasayyengar.pirateballremastered.teams.Team;
import me.shreyasayyengar.pirateballremastered.utils.Utility;
import me.shreyasayyengar.pirateballremastered.utils.worldutils.FloatingItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game implements Listener {

    private final Arena arena;

    public Game(Arena arena) {
        this.arena = arena;
    }

    /**
     * Starts the running instance of {@link Game}.
     */
    public void startGame() {
        teleportTeams();
        loadPlayer();
        registerGameEvents();
        gameBegunMessages();
    }

    /**
     * Sends string messages to all online {@link GamePlayer}s.
     */
    private void gameBegunMessages() {
        getBukkitPlayers().forEach(player -> player.sendMessage(Utility.colourise("&eGame started!")));
        getBukkitPlayers().forEach(player -> player.sendTitle(Utility.colourise("&aGame Started!"), null, 0, 200, 0));

        // TODO: send objectives move to Arena class
    }

    /**
     * Teleports all online {@link GamePlayer}s to their Team spawn.
     */
    private void teleportTeams() {
        arena.getGamePlayers().forEach(gamePlayer -> {
            if (gamePlayer.isOnline()) {
                gamePlayer.toBukkitPlayer().teleport(gamePlayer.getTeam().getData().getSpawn());
            }
        });
    }

    /**
     * Sets the player ready for a game. (Clears inventory, Applies Team Armor...)
     */
    private void loadPlayer() {
        arena.getGamePlayers().forEach(gamePlayer -> {
            gamePlayer.toBukkitPlayer().getInventory().clear();
            gamePlayer.toBukkitPlayer().closeInventory();
            gamePlayer.applyArmor();
        });
    }

    public void endGame(Team team) {
        // todo: do end game stuff
        arena.setGameState(GameState.WON);
    }

    /**
     * Determines if a live instance of {@link Game} is ready to move to change {@link GameState}.
     *
     * @return If the game should proceed.
     */
    public boolean beginWaiting() {

        boolean shouldStart = false;

        if (Arena.REQUIRED_PLAYERS == arena.getGamePlayers().size()) {
            arena.setGameState(GameState.COUNTDOWN);
            shouldStart = true;
        }

        return shouldStart;
    }

    // Getters ---------------------------------------------------------------------------------------
    public Arena getArena() {
        return arena;
    }

    /**
     * Should only be called to reference the whole arena
     * and not a {@link GamePlayer}.
     *
     * @return {@link List<Player>} of Bukkit players.
     */
    // TODO move to arena class
    private List<Player> getBukkitPlayers() {

        List<Player> bukkitPlayers = new ArrayList<>();

        arena.getGamePlayers().forEach(gamePlayer -> {
            if (gamePlayer.isOnline()) {
                Player player = gamePlayer.toBukkitPlayer();

                bukkitPlayers.add(player);
            }
        });

        return bukkitPlayers;
    }

    // Game Listeners --------------------------------------------------------------------------------

    /**
     * Registers the listeners & events of a running instance of {@link Game}.
     * <p>
     * Starts the {@link BukkitRunnable} task for updating all current {@link FloatingItem}s in
     * the game.
     */
    private void registerGameEvents() {
        PirateBallPlugin.getInstance().getServer().getPluginManager().registerEvents(this, PirateBallPlugin.getInstance());
        GameMechanicsUtil.enableFloatingItems();
    }

    /**
     * All events should guarantee a valid instance of {@link GamePlayer} and {@link Arena}, but
     *
     * @throws ArenaNotFoundException      if an {@link Arena} was not found and
     * @throws GamePlayerNotFoundException if a {@link GamePlayer} was not found.
     */

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerfromUUID(eventPlayer.getUniqueId());

        if (GameMechanicsUtil.isValidSteal(event.getBlock(), gamePlayer)) {
            GameMechanicsUtil.dropBall(event.getBlock().getLocation(), gamePlayer);
            GameMechanicsUtil.processValidSteal(gamePlayer);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerfromUUID(eventPlayer.getUniqueId());

//        if (eventPlayer.getInventory().getItemInMainHand().isSimilar(gamePlayer.getTeam().getBallData().getBall())) {
        if (eventPlayer.getInventory().getItemInMainHand().getType().equals(Material.PLAYER_HEAD)) {
            event.setCancelled(true);
        }
        GameMechanicsUtil.collectStolenBall(event.getBlock(), gamePlayer);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerfromUUID(eventPlayer.getUniqueId());

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getItem().getType() == Material.PLAYER_HEAD) {

                Location plus1 = new Location(eventPlayer.getWorld(), eventPlayer.getLocation().getX(), eventPlayer.getLocation().getY() + 1, eventPlayer.getLocation().getZ());
                Vector vector = eventPlayer.getLocation().getDirection().multiply(1.2);

                Snowball ball = eventPlayer.launchProjectile(Snowball.class);
                ball.setItem(gamePlayer.getTeam().getBallData().getBall());
                ball.setVelocity(vector);
            }
        }
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {

        System.out.println(((ThrowableProjectile) event.getEntity()).getItem().getType());
        if (event.getHitEntity() instanceof Player victim && event.getEntity().getShooter() instanceof Player shooter) {

            if (!GameManager.isPlaying(victim) && !GameManager.isPlaying(shooter)) return;

            if (GameManager.getArena(victim).equals(GameManager.getArena(shooter))) {

                Arena commonArena = GameManager.getArena(victim);

                GamePlayer victimGamePlayer = commonArena.gamePlayerfromUUID(victim.getUniqueId());
                Team victimTeam = victimGamePlayer.getTeam();

                GamePlayer shooterGamePlayer = commonArena.gamePlayerfromUUID(shooter.getUniqueId());
                Team shooterTeam = shooterGamePlayer.getTeam();

                GameMechanicsUtil.setDeath(victimGamePlayer, GameMechanicsUtil.RespawnReason.HIT_BY_BALL);

                commonArena.broadcast(victimTeam.getChatColorString() + victimGamePlayer.toBukkitPlayer().getName() + " &7was hit by a ball shot from " + shooterTeam.getChatColorString() + shooterGamePlayer.toBukkitPlayer().getName() + "&7!");
            }

        }

    }

    @EventHandler
    private void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerfromUUID(eventPlayer.getUniqueId());

        GameMechanicsUtil.setDeath(gamePlayer, GameMechanicsUtil.RespawnReason.RECONNECTED);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerfromUUID(eventPlayer.getUniqueId());

        arena.broadcast(gamePlayer.getTeam().getData().getChatString() + gamePlayer.toBukkitPlayer().getName() + "&c disconnected!");

        GameMechanicsUtil.handleDisconnection(gamePlayer);
    }

    @EventHandler
    private void onFoodLoss(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerHandSwitch(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }


    public static class GameMechanicsUtil {

        private static final List<GamePlayer> CURRENTLY_RESPAWNING = new ArrayList<>();

        public static void enableFloatingItems() {
            new BukkitRunnable() {
                @Override
                public void run() {
                    FloatingItem.getFloatingItems().stream().filter(i -> i.getArmorStand() != null).forEach(FloatingItem::update);
                }
            }.runTaskTimerAsynchronously(PirateBallPlugin.getInstance(), 0, 1);
        }

        public static boolean isValidSteal(Block block, GamePlayer player) {

            if (block.getRelative(BlockFace.DOWN).getType().equals(player.getTeam().getWool())) {
                if (!player.getTeam().getRegion().isInRegion(block.getLocation())) {
                    return true;
                } else {
                    player.toBukkitPlayer().sendMessage(Utility.colourise("&cYou cannot steal balls at your own base!"));
                }
            }

            return false;
        }

        public static void processValidSteal(GamePlayer player) {
            Team gamePlayerTeam = player.getTeam();
            Team enemyTeam = player.getTeamRegionIn();
            Player bukkitPlayer = player.toBukkitPlayer();

            // player specific actions
            bukkitPlayer.sendTitle(Utility.colourise("&7Ball Stolen!"), Utility.colourise("&8You stole " + gamePlayerTeam.getChatColorString() + "your ball &8from " + enemyTeam.getChatColorString() + enemyTeam.getDisplayName().toLowerCase() + " team!"), 10, 100, 20);
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, 1, 1);


            // player team specific actions
            gamePlayerTeam.sendTeamActionBar(gamePlayerTeam.getChatColorString() + bukkitPlayer.getName() + " has stolen a ball from " + enemyTeam.getChatColorString() + enemyTeam.getDisplayName().toLowerCase() + " team!");


            // enemy team specific actions
            enemyTeam.sendTeamMessage("&4&l[STEAL] " + player.getTeam().getChatColorString() + bukkitPlayer.getName() + " has stolen their ball at your base!");
        }

        public static void collectStolenBall(Block block, GamePlayer gamePlayer) {
            Team team = gamePlayer.getTeam();
            Player player = gamePlayer.toBukkitPlayer();

            if (block.getRelative(BlockFace.DOWN).getType().equals(Material.END_PORTAL_FRAME)) {

                if (gamePlayer.getTeam().getRegion().isInRegion(player.getLocation())) {
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1);
                    block.getRelative(BlockFace.DOWN, 2).setType(gamePlayer.getTeam().getGlass());

                    new FloatingItem(block.getLocation().add(0.5, -1.2, 0.5)).spawn(team.getBallData().getBall(), true);

                } else {
                    player.sendMessage(Utility.colourise("&cYou can only place your ball in your &lteam pod."));
                    Utility.sendActionBar(player, Utility.colourise("&cYou can only place your ball in " + team.getData().getChatString() + "your team pod"));
                }
            } else gamePlayer.toBukkitPlayer().sendMessage(Utility.colourise("&cThat is not where that goes!"));
        }

        public static void dropBall(Location location, GamePlayer player) {
            player.toBukkitPlayer().getWorld().dropItemNaturally(location, player.getTeam().getBallData().getBall());
        }

        public static void handleDisconnection(GamePlayer gamePlayer) {
            Arena currentArena = gamePlayer.getCurrentArena();

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gamePlayer.isOnline()) {
                        currentArena.removePlayer(gamePlayer);
                    }
                }
            }.runTaskLater(PirateBallPlugin.getInstance(), 2400);
        }

        public static void sendToJail(GamePlayer player, Team theJail) {
            Location jail = theJail.getData().getJail();
        }

        public static void setDeath(GamePlayer gamePlayer, RespawnReason reason) {
            CURRENTLY_RESPAWNING.add(gamePlayer);

            Player bukkitPlayer = gamePlayer.toBukkitPlayer();
            @NotNull ItemStack[] armorContents = bukkitPlayer.getInventory().getArmorContents();

            String title = null;
            String subtitle = ChatColor.GRAY + "Moving to jail in ";

            switch (reason) {
                case KILLED -> title = "&cYou died to " + Objects.requireNonNull(gamePlayer.toBukkitPlayer().getLastDamageCause()).getEntity().getName();

                case HIT_BY_BALL -> title = "&cYou've been hit by a ball!";

                case REMOVED_FROM_JAIL -> {
                    title = "&aYou teammate has saved you!";
                    subtitle = "&7Returning to base in ";
                }

                case RECONNECTED -> {
                    title = "&6Reconnected to the game!";
                    subtitle = "&7Returning to base in ";
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
                        bukkitPlayer.setGameMode(GameMode.SURVIVAL);
                        bukkitPlayer.getInventory().setArmorContents(armorContents);
                        CURRENTLY_RESPAWNING.remove(gamePlayer);

                        for (Player loopedPlayer : Bukkit.getOnlinePlayers()) {
                            loopedPlayer.showPlayer(PirateBallPlugin.getInstance(), bukkitPlayer);
                        }
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

        public static void applyDeathTitle(GamePlayer gamePlayer, RespawnReason reason) {
            Player bukkitPlayer = gamePlayer.toBukkitPlayer();

            String title = null;
            String subtitle = ChatColor.GRAY + "Moving to jail in ";

            switch (reason) {
                case KILLED -> title = "&cYou died to " + Objects.requireNonNull(gamePlayer.toBukkitPlayer().getLastDamageCause()).getEntity().getName();

                case HIT_BY_BALL -> title = "&cYou've been hit by a ball!";

                case REMOVED_FROM_JAIL -> {
                    title = "&aYou teammate has saved you!";
                    subtitle = "&7Returning to base in ";
                }

                case RECONNECTED -> {
                    title = "&6Reconnected to the game!";
                    subtitle = "&7Returning to base in ";
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

        public static void setSpawningAction(GamePlayer gamePlayer, boolean added) {

            // todo work

            if (added) {

                for (Player player : gamePlayer.getCurrentArena().getBukkitPlayers()) {
                    player.showPlayer(PirateBallPlugin.getInstance(), gamePlayer.toBukkitPlayer());
                }

                gamePlayer.applyArmor();
                gamePlayer.toBukkitPlayer().setGameMode(GameMode.SURVIVAL);
                gamePlayer.toBukkitPlayer().teleport(gamePlayer.getTeam().getData().getSpawn());


            } else {

                for (Player player : gamePlayer.getCurrentArena().getBukkitPlayers()) {
                    player.hidePlayer(PirateBallPlugin.getInstance(), gamePlayer.toBukkitPlayer());
                }

                gamePlayer.toBukkitPlayer().getInventory().clear();
                gamePlayer.toBukkitPlayer().setGameMode(GameMode.SPECTATOR);
                gamePlayer.toBukkitPlayer().teleport(gamePlayer.getTeam().getData().getSpawn());
            }

            gamePlayer.toBukkitPlayer().getActivePotionEffects().clear();


        }


        private enum RespawnReason {
            KILLED,
            HIT_BY_BALL,
            RECONNECTED, REMOVED_FROM_JAIL
        }
    }
}