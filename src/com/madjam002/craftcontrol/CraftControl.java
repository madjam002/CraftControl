package com.madjam002.craftcontrol;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftControl extends JavaPlugin {
    
    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        
        // Register Events
        pm.registerEvents(new InventoryListener(this), this);
    }
    
    @Override
    public void onDisable() {
        
    }
    
}
