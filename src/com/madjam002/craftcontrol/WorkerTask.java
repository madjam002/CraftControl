package com.madjam002.craftcontrol;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.madjam002.craftcontrol.event.FurnaceBurnTickEvent;
import com.madjam002.craftcontrol.event.FurnaceCookTickEvent;

public class WorkerTask extends BukkitRunnable {

    CraftControl plugin;
    
    static BukkitTask task;
    
    private WorkerTask(CraftControl plugin) {
        this.plugin = plugin;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, 0, 5);
    }
    
    public static void start(CraftControl plugin) {
        if (task == null) {
            new WorkerTask(plugin);
        }
    }
    
    public static void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void run() {
        List<World> worlds = Bukkit.getWorlds();
        
        // update each world
        for (World world : worlds) {
            updateWorld(world);
        }
    }
    
    private void updateWorld(World world) {
        Chunk[] chunks = world.getLoadedChunks();
        
        // update each loaded chunk
        for (Chunk chunk : chunks) {
            updateChunk(chunk);
        }
    }
    
    private void updateChunk(Chunk chunk) {
        BlockState[] tileEntities = chunk.getTileEntities();
        
        // update tile entities
        for (BlockState tileEntity : tileEntities) {
            if (tileEntity.getType() == Material.FURNACE || tileEntity.getType() == Material.BURNING_FURNACE) {
                updateFurnace((Furnace) tileEntity);
            }
        }
    }
    
    private void updateFurnace(Furnace furnace) {
        // Update Burn Time
        if (furnace.getBurnTime() > 0) {
            if (furnace.hasMetadata("lastBurnTime")) {
                short lastBurnTime = furnace.getMetadata("lastBurnTime").get(0).asShort();
                
                FurnaceBurnTickEvent event = new FurnaceBurnTickEvent(furnace);
                Bukkit.getServer().getPluginManager().callEvent(event);
                
                if (event.isCancelled()) {
                    furnace.setBurnTime(lastBurnTime);
                }
            }
            
            furnace.setMetadata("lastBurnTime", new FixedMetadataValue(plugin, furnace.getBurnTime()));
        }
        
        // Update Smelt Time
        if (furnace.getInventory().getSmelting() != null) {
            if (furnace.hasMetadata("lastCookTime")) {
                short lastCookTime = furnace.getMetadata("lastCookTime").get(0).asShort();
                
                FurnaceCookTickEvent event = new FurnaceCookTickEvent(furnace);
                Bukkit.getServer().getPluginManager().callEvent(event);
                
                if (event.isCancelled()) {
                    furnace.setCookTime(lastCookTime);
                }
            }
            
            furnace.setMetadata("lastCookTime", new FixedMetadataValue(plugin, furnace.getCookTime()));
        } else if (furnace.hasMetadata("lastCookTime")) {
            // remove last cook time when smelting has finished
            furnace.removeMetadata("lastCookTime", plugin);
        }
    }
    
}
