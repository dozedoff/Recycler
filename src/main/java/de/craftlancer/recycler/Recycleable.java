package de.craftlancer.recycler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Recycleable
{
    private Material inputType;
    private Material rewardType;
    private int rewardamount;
    private double maxdura;
    private double extradura;
    private boolean calcdura;
    
    public Recycleable(Material type, Material rewardType, int rewardamount, int maxdura, int extradura, boolean calcdura)
    {
        this.inputType = type;
        this.rewardType = rewardType;
        this.rewardamount = rewardamount;
        this.maxdura = maxdura;
        this.extradura = extradura;
        this.calcdura = calcdura;
        
        Permission perm = new Permission("recycler.item." + type.name(), PermissionDefault.FALSE);
        perm.addParent(Recycler.WILDCARD_PERMISSION, true);
        
        Bukkit.getPluginManager().addPermission(perm);
    }
    
    public Material getInputType()
    {
        return inputType;
    }
    
    public Material getRewardType()
    {
        return rewardType;
    }
    
    public int getRewardAmount()
    {
        return rewardamount;
    }
    
    public double getMaxDura()
    {
        return maxdura;
    }
    
    public double getExtraDura()
    {
        return extradura;
    }
    
    public boolean isCalcdura()
    {
        return calcdura;
    }
}
