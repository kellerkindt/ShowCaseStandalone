/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-08-16 22:43 +02 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kellerkindt.scs.listeners;

import com.kellerkindt.scs.exceptions.InsufficientPermissionException;
import com.kellerkindt.scs.utilities.Term;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

import com.kellerkindt.scs.ShowCaseStandalone;


public class BlockListener implements Listener{
    private ShowCaseStandalone     scs;

    public BlockListener(ShowCaseStandalone scs) {
        this.scs          = scs;
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPlace (BlockPlaceEvent e) {
        if (scs.getShopHandler().isShopBlock(e.getBlock())) {
            // allow placing blocks to restore glitched shops
            // e.setCancelled(true);

            // still forbid black listed blocks
            if (!e.getBlock().getType().isSolid()
            || !e.getBlock().getType().isBlock()
            ||  scs.getConfiguration().isBlockListBlacklist() == scs.getConfiguration().getBlockListBlocks().contains(e.getBlock().getType().toString())) {
                e.setCancelled(true);
                scs.sendMessage(e.getPlayer(), Term.BLACKLIST_BLOCK.get());
            }

        } else if ( scs.getShopHandler().isShopBlock(e.getBlockPlaced().getLocation().subtract(0, 1, 0).getBlock()) ){
            //This is the block above.
                    //CHeck for attchables for the block being placed.
                    if(!(Material.STEP.equals(e.getBlockPlaced().getLocation().subtract(0, 1, 0).getBlock().getType())))
                        e.setCancelled(true);
                }
        
        
        
//        // Quick and Dirty...
//        else if (e.getBlock().getState() instanceof CraftSign) {
//            try {
//                Block     block    = Utilities.getBlockBehind((Sign)e.getBlock().getState());
//                Shop    shop    = null;
//                
//                if (block != null)
//                    shop = scs.getShopHandler().getShopForBlock(block);
//                
//                scs.performShowCaseChangedEvent(shop, e.getPlayer());
//                    
//            } catch (ShopNotFoundException snfe) { }
//        }
    }
    
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockBreak (BlockBreakEvent e) {    
        if ( scs.getShopHandler().isShopBlock(e.getBlock())) {
            e.setCancelled(true);
        }
    }
    

    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onHangingPlaced (HangingPlaceEvent event) {
        
        // BlockState
        Hanging    hanging = event.getEntity();
        
        // ItemFrame?
        if (hanging instanceof ItemFrame) {
            scs.getShopHandler().addItemFrame((ItemFrame)hanging);
        }
    }
    
    @EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage (EntityDamageEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            ItemFrame frame = (ItemFrame)event.getEntity();
            
            // is the block below a shop block?
            if (scs.getShopHandler().isKnownItemFrame(frame)) {
                frame.setItem(null);
            }
        }
    }
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onHangingBreak (HangingBreakEvent event) {
        
        // BlockState
        Hanging    hanging = event.getEntity();
        
        // ItemFrame?
        if (hanging instanceof ItemFrame) {
            scs.getShopHandler().removeItemFrame((ItemFrame)hanging);
        }
    }
        
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPistonExtend (BlockPistonExtendEvent e) {
        for (Block b : e.getBlocks()) {
            if ( scs.getShopHandler().isShopBlock(b) ) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        if ( scs.getShopHandler().isShopBlock(e.getRetractLocation().getBlock()) ) {
            e.setCancelled(true);
        }
    }

}
