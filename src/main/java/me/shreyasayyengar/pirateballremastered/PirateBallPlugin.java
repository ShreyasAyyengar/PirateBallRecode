package me.shreyasayyengar.pirateballremastered;

import me.shreyasayyengar.pirateballremastered.commands.PirateBallBaseCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class PirateBallPlugin extends JavaPlugin {

    private static PirateBallPlugin INSTANCE;

    @Override
    public void onEnable() {
        PirateBallPlugin.INSTANCE = this;


        registerEvents();
        registerCommands();

    }

    private void registerCommands() {
        this.getCommand("pirateball").setExecutor(new PirateBallBaseCommand());
    }

    private void registerEvents() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PirateBallPlugin getInstance() {
        return INSTANCE;
    }
}
