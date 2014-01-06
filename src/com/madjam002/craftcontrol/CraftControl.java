package com.madjam002.craftcontrol;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftControl extends JavaPlugin {
    
    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        
        // Save Default Configuration
        saveDefaultConfig();
        
        // Helper classes
        PermissionChecker permissionChecker = new PermissionChecker();
        MessageHelper messageHelper = new MessageHelper(this);
        
        // Register Events
        pm.registerEvents(new InventoryListener(this, permissionChecker, messageHelper), this);
    }
    
    @Override
    public void onDisable() {
        
    }
    
}
