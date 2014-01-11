package com.madjam002.craftcontrol;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.madjam002.craftcontrol.listener.CraftingListener;
import com.madjam002.craftcontrol.listener.FurnaceListener;
import com.madjam002.craftcontrol.recipe.RecipeLoader;
import com.madjam002.craftcontrol.util.MessageHelper;
import com.madjam002.craftcontrol.util.PermissionChecker;

public final class CraftControl extends JavaPlugin {
    
    RecipeLoader recipeLoader;
    
    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        
        // Save Default Configuration
        saveDefaultConfig();
        
        // Helper classes
        PermissionChecker permissionChecker = new PermissionChecker();
        MessageHelper messageHelper = new MessageHelper(this);
        
        // Load Custom Recipes
        recipeLoader = new RecipeLoader(this);
        recipeLoader.loadFromConfig();
        
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Loaded " + recipeLoader.getRecipes().size() + " custom recipe(s)");
        
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
        
        // Save and shutdown Recipe Loader
        recipeLoader.shutdown();
    }
    
}
