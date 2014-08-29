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
import org.bukkit.inventory.ItemStack;

public class RecyclerListener implements Listener
{
    private Recycler plugin;
    private Set<Block> noExpBlock;
    
    public RecyclerListener(Recycler instance)
    {
        plugin = instance;
        noExpBlock = new HashSet<Block>();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSmelt(FurnaceSmeltEvent event)
    {
        ItemStack src = event.getSource();
        if (plugin.getRecyleMap().containsKey(src.getType()))
        {
            Recycleable rec = plugin.getRecyleMap().get(src.getType());
            int amount;
            
            if (rec.isCalcdura())
            {
                if ((src.getDurability() + rec.getExtraDura()) > 0)
                    amount = (int) Math.floor(rec.getRewardAmount() * ((rec.getMaxDura() - src.getDurability() + rec.getExtraDura()) / rec.getMaxDura()));
                else
                    amount = rec.getRewardAmount();
                
                if (amount > rec.getRewardAmount())
                    amount = rec.getRewardAmount();
            }
            else
                amount = rec.getRewardAmount();
            
            if (amount != 0 || !plugin.preventZeroOutput)
            {
                event.setResult(new ItemStack(rec.getRewardType(), amount));
                noExpBlock.add(event.getBlock());
            }
            else
                event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceExtract(FurnaceExtractEvent e) 
    {
        Block furnaceBlock = e.getBlock();
        BlockState furnaceState = furnaceBlock.getState();
        
        if (furnaceState instanceof Furnace) 
        {
            ItemStack result = ((Furnace) furnaceState).getInventory().getResult();
             
            if (result != null && noExpBlock.contains(furnaceBlock))
            {
                e.setExpToDrop(0);
                noExpBlock.remove(furnaceBlock);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e)
    {
        if (noExpBlock.contains(e.getBlock()))
            noExpBlock.remove(e.getBlock());
    }
    
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void putInFurnace(InventoryClickEvent event)
    {
        if (!event.getInventory().getType().equals(InventoryType.FURNACE) || event.getRawSlot() == -999)
            return;
        
        if (event.getRawSlot() == 0 && plugin.getRecyleMap().containsKey(event.getCursor().getType()))
        {
            if (!event.getWhoClicked().hasPermission("recycler.item." + event.getCursor().getType().name()))
                event.setCancelled(true);
        }
        else if (event.isShiftClick() && plugin.getRecyleMap().containsKey(event.getCurrentItem().getType()))
            if (!event.getWhoClicked().hasPermission("recycler.item." + event.getCurrentItem().getType().name()))
                event.setCancelled(true);
            else
                ((Player) event.getWhoClicked()).updateInventory();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryMove(InventoryMoveItemEvent e)
    {
        if (!plugin.preventHoppers)
            return;
        
        if (e.getDestination().getType().equals(InventoryType.FURNACE))
            if (e.getSource().getType().equals(InventoryType.HOPPER) || e.getSource().getType().equals(InventoryType.DROPPER))
                if (plugin.getRecyleMap().containsKey(e.getItem().getType()))
                    e.setCancelled(true);
    }
}
