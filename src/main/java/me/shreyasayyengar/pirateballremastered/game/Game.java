package me.shreyasayyengar.pirateballremastered.game;

import me.shreyasayyengar.pirateballremastered.PirateBallPlugin;
import me.shreyasayyengar.pirateballremastered.arena.Arena;
import me.shreyasayyengar.pirateballremastered.exception.ArenaNotFoundException;
import me.shreyasayyengar.pirateballremastered.exception.GamePlayerNotFoundException;
import me.shreyasayyengar.pirateballremastered.teams.Team;
import me.shreyasayyengar.pirateballremastered.utils.Utility;
import me.shreyasayyengar.pirateballremastered.utils.worldutils.FloatingItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
     */
    public void shouldStartCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Arena.REQUIRED_PLAYERS == arena.getGamePlayers().size()) {
                    arena.setGameState(GameState.COUNTDOWN);
                    cancel();
                }
            }
        }.runTaskTimer(PirateBallPlugin.getInstance(), 0L, 10L);
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
    private void onBlockBreak(BlockBreakEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerFromUUID(eventPlayer.getUniqueId());

        if (GameMechanicsUtil.isValidSteal(event.getBlock(), gamePlayer)) {
            GameMechanicsUtil.dropBall(event.getBlock().getLocation(), gamePlayer);
            GameMechanicsUtil.processValidSteal(gamePlayer);
            event.setDropItems(false);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerFromUUID(eventPlayer.getUniqueId());

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
        GamePlayer gamePlayer = arena.gamePlayerFromUUID(eventPlayer.getUniqueId());

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getItem().getType() == Material.PLAYER_HEAD) {

                Vector vector = eventPlayer.getLocation().getDirection().multiply(1.2);

                Snowball ball = eventPlayer.launchProjectile(Snowball.class);
                ball.setItem(gamePlayer.getTeam().getBallData().getBall());
                ball.setVelocity(vector);
            }
        }
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {

        if (event.getHitEntity() instanceof Player victim && event.getEntity().getShooter() instanceof Player shooter) {

            if (!GameManager.isPlaying(victim) && !GameManager.isPlaying(shooter)) return;

            if (victim.getUniqueId().equals(shooter.getUniqueId())) return;

            if (GameManager.getArena(victim).equals(GameManager.getArena(shooter))) {

                Arena commonArena = GameManager.getArena(victim);

                GamePlayer victimGamePlayer = commonArena.gamePlayerFromUUID(victim.getUniqueId());
                Team victimTeam = victimGamePlayer.getTeam();

                GamePlayer shooterGamePlayer = commonArena.gamePlayerFromUUID(shooter.getUniqueId());
                Team shooterTeam = shooterGamePlayer.getTeam();

                if (victimTeam.getDisplayName().equals(shooterTeam.getDisplayName())) {
                    // TODO: transfer ball to new player
                } else {

                    victimGamePlayer.setDespawningAction();
                    victimGamePlayer.applyDeathTitle(GameMechanicsUtil.RespawnReason.HIT_BY_BALL);
                    victimGamePlayer.setSpawningAction(shooterTeam.getData().getJail());

                    commonArena.broadcast(victimTeam.getChatColorString() + victimGamePlayer.toBukkitPlayer().getName() + " &7was hit by a ball shot from " + shooterTeam.getChatColorString() + shooterGamePlayer.toBukkitPlayer().getName() + "&7!");
                }
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
    private void onPlayerJoin(PlayerJoinEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerFromUUID(eventPlayer.getUniqueId());

        GameMechanicsUtil.handleReconnection(gamePlayer);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) throws ArenaNotFoundException, GamePlayerNotFoundException {
        Player eventPlayer = event.getPlayer();

        if (!GameManager.isPlaying(eventPlayer)) return;

        Arena arena = GameManager.getArena(eventPlayer);
        GamePlayer gamePlayer = arena.gamePlayerFromUUID(eventPlayer.getUniqueId());

        GameMechanicsUtil.handleDisconnection(gamePlayer);
    }

    @EventHandler
    private void onFoodLoss(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerHandSwitch(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
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
            gamePlayer.setLastStandingIn(gamePlayer.getTeamRegionIn());
            currentArena.broadcast(gamePlayer.getTeam().getData().getChatString() + gamePlayer.toBukkitPlayer().getName() + "&c disconnected!");

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gamePlayer.isOnline()) {
                        currentArena.removePlayer(gamePlayer);
                    }
                }
            }.runTaskLater(PirateBallPlugin.getInstance(), 2400);
        }

        public static void handleReconnection(GamePlayer gamePlayer) {
            Arena currentArena = gamePlayer.getCurrentArena();
            currentArena.broadcast(gamePlayer.getTeam().getChatColorString() + gamePlayer.toBukkitPlayer().getName() + "&7 has reconnected!");

            if (Objects.equals(gamePlayer.getLastStandingIn().getDisplayName(), gamePlayer.getTeam().getDisplayName())) {
                gamePlayer.applyDeathTitle(RespawnReason.RECONNECTED_SAFE);
                gamePlayer.setDespawningAction();
                gamePlayer.setSpawningAction(gamePlayer.getTeam().getData().getSpawn());
            } else {
                gamePlayer.applyDeathTitle(RespawnReason.RECONNECTED_UNSAFE);
                gamePlayer.setDespawningAction();
                gamePlayer.setSpawningAction(gamePlayer.getLastStandingIn().getData().getJail());
            }
        }

        enum RespawnReason {
            KILLED,
            HIT_BY_BALL,
            RECONNECTED_SAFE,
            RECONNECTED_UNSAFE,
            REMOVED_FROM_JAIL
        }
    }
}