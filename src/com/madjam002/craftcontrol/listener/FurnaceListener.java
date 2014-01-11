package com.madjam002.craftcontrol.listener;

import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.madjam002.craftcontrol.CraftControl;
import com.madjam002.craftcontrol.event.FurnaceCookTickEvent;
import com.madjam002.craftcontrol.util.MessageHelper;
import com.madjam002.craftcontrol.util.PermissionChecker;

public class FurnaceListener implements Listener {
    
    CraftControl plugin;
    PermissionChecker permissionChecker;
    MessageHelper messageHelper;
    
    public FurnaceListener(CraftControl plugin, PermissionChecker permissionChecker, MessageHelper messageHelper) {
        this.plugin = plugin;
        this.permissionChecker = permissionChecker;
        this.messageHelper = messageHelper;
    }
    
    @EventHandler
    public void onFurnaceCookTick(FurnaceCookTickEvent event) {
        Furnace furnace = event.getFurnace();
        
        if (furnace.hasMetadata("currentPlayer")) {
            Player player = (Player) furnace.getMetadata("currentPlayer").get(0).value();
            ItemStack item = furnace.getInventory().getSmelting();
            if (item != null &&
                !permissionChecker.check(player, "smelt", item.getType()) &&
                !item.getType().equals(Material.AIR)
            ) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryType inventoryType = event.getInventory().getType();
        
        if (inventoryType.equals(InventoryType.FURNACE)) {
            if (event.getSlot() == 0) {
                // Change player on furnace
                Furnace furnace = (Furnace) event.getInventory().getHolder();
                furnace.setMetadata("currentPlayer", new FixedMetadataValue(plugin, player));
            }
        }
    }
    
}
