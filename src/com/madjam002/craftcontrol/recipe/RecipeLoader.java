package com.madjam002.craftcontrol.recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import com.madjam002.craftcontrol.CraftControl;

public class RecipeLoader {

    CraftControl plugin;
    FileConfiguration config;
    ArrayList<ShapedRecipe> recipes;
    
    public RecipeLoader(CraftControl plugin) {
        this.plugin = plugin;
        this.recipes = new ArrayList<ShapedRecipe>();
    }
    
    public void loadFromConfig() {
        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/recipes.yml"));
        
        loadRecipes();
    }
    
    public void loadRecipes() {
        Set<String> recipes = config.getKeys(false);
        
        for (String recipeName : recipes) {
            try {
                loadRecipe(recipeName, config.getConfigurationSection(recipeName));
            } catch (IllegalArgumentException e) {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error loading custom recipe '" + recipeName + "': " + e.getMessage());
            }
        }
    }
    
    public void loadRecipe(String name, ConfigurationSection recipeConfig) {
        // check that values are correct
        if (!recipeConfig.isString("result.item")) {
            throw new IllegalArgumentException("result.item must be a string");
        }
        if (recipeConfig.contains("result.amount") && !recipeConfig.isInt("result.amount")) {
            throw new IllegalArgumentException("result.amount must be an integer");
        }
        if (!recipeConfig.isList("shape")) {
            throw new IllegalArgumentException("shape must be an array of strings");
        }
        if (!recipeConfig.isConfigurationSection("ingredients")) {
            throw new IllegalArgumentException("ingredients must be a key value pair of ingredients");
        }
        if (recipeConfig.contains("enchantments") && !recipeConfig.isList("enchantments")) {
            throw new IllegalArgumentException("enchantments must be an array of enchantment objects");
        }
        
        // get material from item string
        Material resultItemMaterial = Material.getMaterial(recipeConfig.getString("result.item").toUpperCase());
        if (resultItemMaterial == null) {
            throw new IllegalArgumentException("result.item must be a valid bukkit material name");
        }
        
        // get amount
        int amount = 1;
        if (recipeConfig.contains("result.amount")) {
            amount = recipeConfig.getInt("result.amount");
        }
        
        // create item stack
        ItemStack result = new ItemStack(resultItemMaterial, amount);
        
        // enchantments
        if (recipeConfig.isList("enchantments")) {
            List<Map<?, ?>> enchantments = recipeConfig.getMapList("enchantments");
            
            for (Map<?, ?> enchantmentConfig : enchantments) {
                if (!enchantmentConfig.containsKey("type")) {
                    throw new IllegalArgumentException("enchantment.type must be a valid bukkit material name");
                }
                if (!enchantmentConfig.containsKey("level")) {
                    throw new IllegalArgumentException("enchantment.level must be an integer");
                }
                
                Enchantment enchantment = Enchantment.getByName(((String) enchantmentConfig.get("type")).toUpperCase());
                if (enchantment == null) {
                    throw new IllegalArgumentException("enchantment.type must be mapped to a valid enchantment name");
                }
                
                result.addEnchantment(enchantment, (Integer) enchantmentConfig.get("level"));
            }
        }
        
        // create recipe object
        ShapedRecipe recipe = new ShapedRecipe(result);
        
        // set the shape of the recipe from the shape array
        String[] shape = recipeConfig.getList("shape").toArray(new String[recipeConfig.getList("shape").size()]);
        recipe.shape(shape);
        
        // add the ingredients of the recipe
        Set<String> ingredients = recipeConfig.getConfigurationSection("ingredients").getKeys(false);
        for (String ingredientKey : ingredients) {
            if (ingredientKey.length() != 1 || !recipeConfig.isString("ingredients." + ingredientKey)) {
                throw new IllegalArgumentException("Invalid ingredient key: " + ingredientKey);
            }
            
            Material ingredient = Material.getMaterial(recipeConfig.getString("ingredients." + ingredientKey).toUpperCase());
            if (ingredient == null) {
                throw new IllegalArgumentException("ingredients." + ingredientKey + " must be mapped to a valid bukkit material name");
            }
            
            recipe.setIngredient(ingredientKey.charAt(0), ingredient);
        }
        
        // finally, add the recipe to the server
        plugin.getServer().addRecipe(recipe);
        
        recipes.add(recipe);
    }
    
    public List<ShapedRecipe> getRecipes() {
        return recipes;
    }
    
    public void save() {
        try {
            config.save(new File(plugin.getDataFolder() + "/recipes.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void shutdown() {
        plugin.getServer().clearRecipes();
        recipes.clear();
    }
    
}
