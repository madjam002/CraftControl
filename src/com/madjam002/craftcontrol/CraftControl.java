package com.madjam002.craftcontrol;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.madjam002.craftcontrol.listener.CraftingListener;
import com.madjam002.craftcontrol.listener.FurnaceListener;
import com.madjam002.craftcontrol.util.MessageHelper;
import com.madjam002.craftcontrol.util.PermissionChecker;

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
        pm.registerEvents(new CraftingListener(this, permissionChecker, messageHelper), this);
        pm.registerEvents(new FurnaceListener(this, permissionChecker, messageHelper), this);
        
        // Start Worker
        WorkerTask.start(this);
    }
    
    @Override
    public void onDisable() {
        // Stop Worker
        WorkerTask.stop();
    }
    
}
