package com.madjam002.craftcontrol.event;

import org.bukkit.block.Furnace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FurnaceBurnTickEvent extends Event implements Cancellable {
    
    static final HandlerList handlers = new HandlerList();
    boolean cancelled;
    Furnace furnace;
    
    public FurnaceBurnTickEvent(Furnace furnace) {
        this.furnace = furnace;
    }
    
    public Furnace getFurnace() {
        return furnace;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
 
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
