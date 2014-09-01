package de.craftlancer.recycler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import org.mcstats.Metrics;

public class Recycler extends JavaPlugin
{
    public static final Permission WILDCARD_PERMISSION = new Permission("recycler.item.*", PermissionDefault.FALSE);
    
    //used to let MC know, that a recipe accepts all data 
    //this is implementation specific and may cause problems in older or future versions!
    public static final int MATCH_ALL_DATA = 32767;
    
    private HashMap<Material, Recycleable> map = new HashMap<Material, Recycleable>();
    private FileConfiguration config;
    private boolean preventHoppers = true;
    private boolean preventZeroOutput = true;
    
    @Override
    public void onEnable()
    {
        loadConfig();
        getServer().getPluginManager().addPermission(WILDCARD_PERMISSION);
        getServer().getPluginManager().registerEvents(new RecyclerListener(this), this);
        
        try
        {
            Metrics metrics = new Metrics(this);
            metrics.start();
        }
        catch (IOException e)
        {
        }
    }
    
    @Override
    public void onDisable()
    {
        map.clear();
        config = null;
        getServer().getScheduler().cancelTasks(this);
    }
    
    @SuppressWarnings("deprecation")
    private void loadConfig()
    {
        if (!new File(getDataFolder().getPath(), "config.yml").exists())
            saveDefaultConfig();
        
        reloadConfig();
        
        config = getConfig();
        map.clear();
        
        preventZeroOutput = config.getBoolean("preventZeroOutput", true);
        preventHoppers = config.getBoolean("disableHopper", true);
        
        for (String key : config.getKeys(false))
            if (!key.equals("disableHopper") && !key.equals("preventZeroOutput"))
            {
                Material inputType = Material.matchMaterial(config.getString(key + ".id"));
                Material rewardType = Material.matchMaterial(config.getString(key + ".rewardid"));
                int amount = config.getInt(key + ".rewardamount", 0);
                int maxdura = config.getInt(key + ".maxdura", 0);
                int extradura = config.getInt(key + ".extradura", 0);
                boolean calcdura = config.getBoolean(key + ".calcdura", true);
                
                if (inputType == null)
                    getLogger().warning("Invalid Material: " + config.getString(key + ".id"));
                else if (rewardType == null)
                    getLogger().warning("Invalid Material: " + config.getString(key + ".rewardid"));
                else if (map.put(inputType, new Recycleable(inputType, rewardType, amount, maxdura, extradura, calcdura)) != null)
                    getLogger().warning("You have 2 configs for " + inputType.name() + "! Using the last one.");
            }
        
        getLogger().info(map.size() + " recycleables loaded.");
        
        for (Recycleable rec : map.values())
            getServer().addRecipe(new FurnaceRecipe(new ItemStack(rec.getRewardType()), rec.getInputType(), MATCH_ALL_DATA));
    }
    
    public boolean isZeroOutputDisabled()
    {
        return preventZeroOutput;
    }
    
    public boolean isHopperDisabled()
    {
        return preventHoppers;
    }
    
    public HashMap<Material, Recycleable> getRecyleMap()
    {
        return map;
    }
    
    public boolean hasRecycleable(Material mat)
    {
        return map.containsKey(mat);
    }
    
    public Recycleable getRecycleable(Material mat)
    {
        return map.get(mat);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (sender.hasPermission("recycler.admin"))
            loadConfig();
        
        return true;
    }
}
