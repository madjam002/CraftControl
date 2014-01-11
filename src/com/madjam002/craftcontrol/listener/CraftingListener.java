package com.madjam002.craftcontrol.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.madjam002.craftcontrol.CraftControl;
import com.madjam002.craftcontrol.util.MessageHelper;
import com.madjam002.craftcontrol.util.PermissionChecker;

public class CraftingListener implements Listener {
    
    CraftControl plugin;
    PermissionChecker permissionChecker;
    MessageHelper messageHelper;
    
    public CraftingListener(CraftControl plugin, PermissionChecker permissionChecker, MessageHelper messageHelper) {
        this.plugin = plugin;
        this.permissionChecker = permissionChecker;
        this.messageHelper = messageHelper;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        ItemStack result = event.getInventory().getResult();
        
        if (!permissionChecker.check(player, "craft", result.getType())) {
            // Change display name
            ItemMeta meta = result.getItemMeta();
            meta.setDisplayName(ChatColor.RED + messageHelper.getMessage("messages.craft.denied", result.getType(), "You cannot craft this item"));
            result.setItemMeta(meta);
            
            // Update crafting result
            event.getInventory().setResult(result);
        }
    }
    
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getView().getPlayer();
        ItemStack result = event.getInventory().getResult();
        
        if (!permissionChecker.check(player, "craft", result.getType())) {
            // Cancel movement of forbidden item
            event.setCancelled(true);
        }
    }
    
}
