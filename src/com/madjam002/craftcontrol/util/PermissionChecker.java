package com.madjam002.craftcontrol.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class PermissionChecker {
    
    public boolean check(Player player, String kind, Material type) {
        boolean allowed = true;
        
        if (player.isPermissionSet(String.format("craftcontrol.%s.*", kind)) && !player.hasPermission(String.format("craftcontrol.%s.*", kind))) {
            allowed = false;
        }

        if (player.isPermissionSet(String.format("craftcontrol.%s.%s", kind, type.name()))) {
            allowed = player.hasPermission(String.format("craftcontrol.%s.%s", kind, type.name()));
        }
        
        return allowed;
    }
    
}
