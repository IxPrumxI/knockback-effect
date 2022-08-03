package com.blocksmc;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getLogger().info("Plugin Enabled.");
        this.getServer().getPluginManager().registerEvents(new ExplosionEffectListener(), this);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Plugin Disabled.");
    }
}