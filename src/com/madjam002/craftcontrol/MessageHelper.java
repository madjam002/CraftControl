package com.madjam002.craftcontrol;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public final class MessageHelper {
    
    CraftControl plugin;
    
    public MessageHelper(CraftControl plugin) {
        this.plugin = plugin;
    }
    
    public String getMessage(String path, Material type, String defaultMessage) {
        String typeName = type.name();
        
        FileConfiguration config = plugin.getConfig();
        
        // Check if node is string
        if (config.isString(path)) {
            return config.getString(path);
        }
        
        // Check type node
        if (config.isString(path + "." + typeName)) {
            return config.getString(path + "." + typeName);
        }
        
        // Check type node lowercase
        if (config.isString(path + "." + typeName.toLowerCase())) {
            return config.getString(path + "." + typeName.toLowerCase());
        }
        
        // Check default
        if (config.isString(path + ".default")) {
            return config.getString(path + ".default");
        }
        
        // Return default if none found
        return defaultMessage;
    }

}
