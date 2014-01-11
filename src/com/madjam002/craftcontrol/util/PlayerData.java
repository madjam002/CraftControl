package com.madjam002.craftcontrol.util;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerData {
    
    HashMap<String, HashMap<String, Object>> data;
    
    public PlayerData() {
        data = new HashMap<String, HashMap<String, Object>>();
    }
    
    public HashMap<String, Object> getDataForPlayer(Player player) {
        if (!data.containsKey(player.getName())) {
            data.put(player.getName(), new HashMap<String, Object>());
        }
        
        return data.get(player.getName());
    }
    
    public Object getData(Player player, String key) {
        return getDataForPlayer(player).get(key);
    }
    
    public void setData(Player player, String key, Object value) {
        HashMap<String, Object> data = getDataForPlayer(player);
        
        if (data.containsKey(key)) {
            data.remove(key);
        }
        data.put(key, value);
    }
    
    public void removeData(Player player, String key) {
        getDataForPlayer(player).remove(key);
    }

}
