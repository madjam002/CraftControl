package com.madjam002.craftcontrol;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
    
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        if (command.getName().equalsIgnoreCase("basic")) {
            sender.sendMessage("Why hello!");
            return true;
        }
        
        return false;
    }
    
    
    
}
