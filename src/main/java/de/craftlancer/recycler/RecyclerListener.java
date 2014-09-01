package de.craftlancer.recycler;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class RecyclerListener implements Listener
{
    private static final int INPUT_SLOT = 0;
    private static final int OUTSIDE_SLOT = -999;
    
    private Recycler plugin;
    private Set<Block> noExpBlock;
    
    public RecyclerListener(Recycler instance)
    {
        plugin = instance;
        noExpBlock = new HashSet<Block>();
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSmelt(FurnaceSmeltEvent event)
    {
        ItemStack src = event.getSource();
        
        if (!plugin.hasRecycleable(src.getType()))
            return;
        
        Recycleable rec = plugin.getRecycleable(src.getType());
        
        int amount = rec.calculateAmount(src);
        
        if (amount != 0)
        {
            event.setResult(new ItemStack(rec.getRewardType(), amount));
            noExpBlock.add(event.getBlock());
        }
        else
        {
            if (!plugin.isZeroOutputDisabled())
            {
                FurnaceInventory inventory = ((Furnace) event.getBlock().getState()).getInventory();
                ItemStack smelting = inventory.getSmelting();
                
                if (smelting.getAmount() <= 1)
                    inventory.setSmelting(null);
                else
                {
                    smelting.setAmount(smelting.getAmount() - 1);
                    inventory.setSmelting(smelting);
                }
            }
            
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onFurnaceExtract(FurnaceExtractEvent e)
    {
        Block furnaceBlock = e.getBlock();
        BlockState furnaceState = furnaceBlock.getState();
        
        if (!(furnaceState instanceof Furnace))
            return;
        
        if (!noExpBlock.contains(furnaceBlock))
            return;
        
        if (e.getItemType() != null)
            e.setExpToDrop(0);
        
        noExpBlock.remove(furnaceBlock);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e)
    {
        if (noExpBlock.contains(e.getBlock()))
            noExpBlock.remove(e.getBlock());
    }
    
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void putInFurnace(InventoryClickEvent event)
    {
        if (!event.getInventory().getType().equals(InventoryType.FURNACE) || event.getRawSlot() == OUTSIDE_SLOT)
            return;
        
        ItemStack item = null;
        
        if (event.getRawSlot() == INPUT_SLOT)
            item = event.getCursor();
        else if (event.isShiftClick())
            item = event.getCurrentItem();
        else
            return;
        
        if (item == null || !plugin.hasRecycleable(item.getType()))
            return;
        
        Recycleable rec = plugin.getRecycleable(item.getType());
        
        if (!event.getWhoClicked().hasPermission(rec.getPermission()))
            event.setCancelled(true);
        else if (event.isShiftClick())
            ((Player) event.getWhoClicked()).updateInventory();
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryMove(InventoryMoveItemEvent e)
    {
        if (!plugin.isHopperDisabled())
            return;
        
        InventoryType source = e.getSource().getType();
        
        if (!source.equals(InventoryType.HOPPER) && !source.equals(InventoryType.DROPPER))
            return;
        
        if (!e.getDestination().getType().equals(InventoryType.FURNACE))
            return;
        
        if (!plugin.hasRecycleable(e.getItem().getType()))
            return;
        
        e.setCancelled(true);
    }
}
