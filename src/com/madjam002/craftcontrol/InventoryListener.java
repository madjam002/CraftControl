package com.madjam002.craftcontrol;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
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
            // Change display name and remove lores
            ItemMeta meta = result.getItemMeta();
            meta.setDisplayName(ChatColor.RED + messageHelper.getMessage("messages.craft.denied", result.getType(), "You cannot craft this item"));
            meta.setLore(new ArrayList<String>());
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
        
        if (inventoryType.equals(InventoryType.FURNACE)) {
            Furnace furnace = (Furnace) event.getInventory().getHolder();

            ItemStack result = event.getCurrentItem();
            if (result != null && !result.getType().equals(Material.AIR) && slotType.equals(SlotType.RESULT)) {
                ItemMeta meta = result.getItemMeta();
                if (meta.hasLore()) {
                    // Change display name
                    meta.setDisplayName(meta.getLore().get(0));
                    meta.setLore(null);
                    result.setItemMeta(meta);
                    
                    event.setCurrentItem(result);
                }
            }
        }
    }
 
    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        ItemStack source = furnace.getInventory().getItem(0);
        
        // Change display name
        final ItemMeta meta = source.getItemMeta();
        meta.setLore(new ArrayList<String>() {{ add(meta.getDisplayName()); }});
        meta.setDisplayName(ChatColor.RED + messageHelper.getMessage("messages.smelt.denied", source.getType(), "You cannot smelt this item"));
        source.setItemMeta(meta);
        
        // Update smelting result
        furnace.getInventory().setSmelting(new ItemStack(Material.AIR));
        event.setCancelled(true);
        
        furnace.getInventory().setResult(source);
    }
    
}
