package me.shreyasayyengar.pirateballremastered;

import me.shreyasayyengar.pirateballremastered.commands.PirateBallBaseCommand;
import me.shreyasayyengar.pirateballremastered.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PirateBallPlugin extends JavaPlugin {

    private static PirateBallPlugin INSTANCE;

    @Override
    public void onEnable() {
        PirateBallPlugin.INSTANCE = this;

        registerGlobalEvents();
        registerCommands();
        ConfigManager.init(this);

    }

    private void registerCommands() {
        this.getCommand("pirateball").setExecutor(new PirateBallBaseCommand());
    }

    private void registerGlobalEvents() {

    }

    @Override
    public void onDisable() {
    }

    public static PirateBallPlugin getInstance() {
        return INSTANCE;
    }
}
