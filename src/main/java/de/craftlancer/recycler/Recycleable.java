package de.craftlancer.recycler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Recycleable
{
    private final Material inputType;
    private final Material rewardType;
    private final int rewardamount;
    private final double maxdura;
    private final double extradura;
    private final boolean calcdura;
    
    private final Permission perm;
    
    public Recycleable(Material type, Material rewardType, int rewardamount, int maxdura, int extradura, boolean calcdura)
    {
        this.inputType = type;
        this.rewardType = rewardType;
        this.rewardamount = rewardamount;
        this.maxdura = maxdura;
        this.extradura = extradura;
        this.calcdura = calcdura;
        
        perm = new Permission("recycler.item." + type.name(), PermissionDefault.FALSE);
        perm.addParent(Recycler.WILDCARD_PERMISSION, true);
        
        if (Bukkit.getPluginManager().getPermission(perm.getName()) == null)
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
    
    public double getMaxDurability()
    {
        return maxdura;
    }
    
    public double getExtraDurability()
    {
        return extradura;
    }
    
    public boolean isCalcdura()
    {
        return calcdura;
    }
    
    public int calculateAmount(ItemStack src)
    {
        if (!isCalcdura())
            return getRewardAmount();
        
        int amount = 0;
        
        if (src.getDurability() + getExtraDurability() >= 0)
            amount = (int) Math.floor(getRewardAmount() * ((getMaxDurability() - src.getDurability() + getExtraDurability()) / getMaxDurability()));
        
        return amount > getRewardAmount() ? getRewardAmount() : amount;
    }

    public Permission getPermission()
    {
        return perm;
    }
}
