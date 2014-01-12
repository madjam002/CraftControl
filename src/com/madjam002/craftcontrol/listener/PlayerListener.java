package com.madjam002.craftcontrol.listener;

import net.minecraft.server.v1_7_R1.CraftingManager;
import net.minecraft.server.v1_7_R1.InventoryCrafting;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.madjam002.craftcontrol.CraftControl;
import com.madjam002.craftcontrol.util.PlayerData;

public class PlayerListener implements Listener {
    
    CraftControl plugin;
    PlayerData playerData;
    
    public PlayerListener(CraftControl plugin, PlayerData playerData) {
        this.plugin = plugin;
        this.playerData = playerData;
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = (Player) event.getPlayer();
        
        if (entity.getType().equals(EntityType.ITEM_FRAME)) {
            ItemFrame itemFrame = (ItemFrame) entity;
            ItemStack holding = player.getItemInHand();
            
            if (itemFrame.hasMetadata("craftingStation")) {
                
                ItemStack[] contents = (ItemStack[]) itemFrame.getMetadata("craftingStation").get(0).value();
                InventoryView inventoryView = player.openWorkbench(null, true);
                CraftInventoryCrafting inventory = (CraftInventoryCrafting) inventoryView.getTopInventory(); 
                
                inventory.setMatrix(contents);
                
                // BEGIN MINECRAFT SERVER
                inventory.setResult(CraftItemStack.asBukkitCopy(CraftingManager.getInstance().craft(
                    (InventoryCrafting) inventory.getMatrixInventory(),
                    ((CraftWorld) player.getWorld()).getHandle()
                )));
                // END MINECRAFT SERVER
                
                event.setCancelled(true);
                playerData.setData(player, "insideCraftingStation", inventoryView);
                
            } else if (holding != null && holding.getType().equals(Material.WORKBENCH)) {
                
                Material material = itemFrame.getItem().getType();
                if (material != null && !material.equals(Material.AIR)) {
                    player.sendMessage("[CraftControl]" + ChatColor.BLUE + " Please enter the crafting recipe you would like to use at this station");
                    InventoryView inventoryView = player.openWorkbench(null, true);
                    playerData.setData(player, "creatingCraftingStation", inventoryView);
                    playerData.setData(player, "creatingCraftingStationItemFrame", itemFrame);
                } else {
                    player.sendMessage("[CraftControl]" + ChatColor.RED + " You need to add an item to the item frame first");
                }
                
                event.setCancelled(true);
                
            }
            
            
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        
        Object creatingCraftingStation = playerData.getData(player, "creatingCraftingStation");
        Object creatingCraftingStationItemFrame = playerData.getData(player, "creatingCraftingStationItemFrame");
        Object insideCraftingStation = playerData.getData(player, "insideCraftingStation");
        
        if (creatingCraftingStation != null && creatingCraftingStationItemFrame != null && creatingCraftingStation.equals(event.getView())) {
            ItemFrame itemFrame = (ItemFrame) creatingCraftingStationItemFrame;
            InventoryView inventoryView = (InventoryView) creatingCraftingStation;
            CraftingInventory inventory = (CraftingInventory) inventoryView.getTopInventory(); 
            
            itemFrame.setMetadata("craftingStation", new FixedMetadataValue(plugin, inventory.getMatrix().clone()));
            
            player.sendMessage("[CraftControl]" + ChatColor.GREEN + " Created new Crafting Station!");
            
            playerData.removeData(player, "creatingCraftingStation");
            playerData.removeData(player, "creatingCraftingStationItemFrame");
        } else if (insideCraftingStation != null) {
            CraftingInventory inventory = (CraftingInventory) event.getInventory();

            inventory.clear();
            
            playerData.removeData(player, "insideCraftingStation");
        }
    }
    
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getView().getPlayer();
        Object creatingCraftingStation = playerData.getData(player, "creatingCraftingStation");
        Object creatingCraftingStationItemFrame = playerData.getData(player, "creatingCraftingStationItemFrame");
        
        if (creatingCraftingStation != null && creatingCraftingStationItemFrame != null && creatingCraftingStation.equals(event.getView())) {
            ItemFrame itemFrame = (ItemFrame) creatingCraftingStationItemFrame;
            InventoryView inventoryView = (InventoryView) creatingCraftingStation;
            CraftingInventory inventory = (CraftingInventory) inventoryView.getTopInventory(); 
            
            itemFrame.setMetadata("craftingStation", new FixedMetadataValue(plugin, inventory.getMatrix().clone()));
            
            player.sendMessage("[CraftControl]" + ChatColor.GREEN + " Created new Crafting Station!");
            
            event.setCancelled(true);
            event.getView().close();
            
            playerData.removeData(player, "creatingCraftingStation");
            playerData.removeData(player, "creatingCraftingStationItemFrame");
        }
    }
    
}
