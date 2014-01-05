package com.madjam002.craftcontrol;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class PermissionChecker {
    
    public static boolean check(Player player, String kind, Material type) {
        return player.hasPermission(String.format("craftcontrol.%s.%s", kind, type.name()));
    }
    
}
