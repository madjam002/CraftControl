package com.madjam002.craftcontrol;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {
    
    CraftControl plugin;
    PermissionChecker permissionChecker;
    MessageHelper messageHelper;
    
    public InventoryListener(CraftControl plugin, PermissionChecker permissionChecker, MessageHelper messageHelper) {
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
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryType inventoryType = event.getInventory().getType();
        SlotType slotType = event.getSlotType();
        int slotIndex = event.getSlot();
        
        // Smelting
        if (inventoryType.equals(InventoryType.FURNACE) && slotType.equals(SlotType.CONTAINER)) {
            ItemStack object = null;
            if (event.isShiftClick()) {
                object = event.getCurrentItem();
            } else if (slotIndex == 0 && event.getCursor() != null) {
                object = event.getCursor();
            } else if (slotIndex == 1 && event.getInventory().getItem(0) != null) {
                object = event.getInventory().getItem(0);
            }
            
            if (object != null && !permissionChecker.check(player, "smelt", object.getType())) {
                // Cancel smelting of the forbidden item
                event.setCancelled(true);
            }
        }
    }

}
