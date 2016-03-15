/**
* ShowCaseStandalone
* Copyright (C) 2012 Kellerkindt <copyright at kellerkindt.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.kellerkindt.scs.listeners;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.kellerkindt.scs.events.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;
import com.kellerkindt.scs.utilities.Utilities;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {
    
    private ShowCaseStandalone    scs;
    
    public SignListener (ShowCaseStandalone scs) {
        this.scs    = scs;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onPlayerInteract (PlayerInteractEvent pie) {
        // only right click, left click is still required to destroy the sign
        if (pie.getAction() != Action.RIGHT_CLICK_BLOCK && pie.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block block = pie.getClickedBlock();

        // block needs to be a sign
        if (block.getState() instanceof Sign) {
            Sign  sign   = (Sign)block.getState();
            Block behind = Utilities.getBlockBehind(sign);
            Shop  shop   = scs.getShopHandler().getShop(behind);

            if (shop != null) {
                ShowCaseEvent event = null;

                switch (pie.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        event = new ShowCaseInteractEvent(pie.getPlayer(), shop, true);
                        break;

                    case LEFT_CLICK_BLOCK:
                        event = new ShowCaseInfoEvent(pie.getPlayer(), shop);
                        break;
                }


                // call the ShowCaseInteractEvent as if clicked on the actual block
                if (event != null && !scs.callShowCaseEvent(event, pie.getPlayer())) {
                    // cancel whatever would have happened if this sign would not be a shop-sign
                    pie.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onShowCaseInteractEvent (ShowCaseInteractEvent event) {
        if (event.getPlayer().getItemInHand() != null) {
            if (event.getPlayer().getItemInHand().getType() == Material.SIGN && event.hasRightClicked()) {
                // cancel the event and allow a player to place the sign
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChanged (SignChangeEvent event) {
        
        if (!(event.getBlock().getState() instanceof Sign)) {
            scs.getLogger().severe("Sign change event - expected instanceof Sign, got="+event.getBlock().getState());
            return;
        }
        
        // get the block information
        Sign    sign    = (Sign)event.getBlock().getState();
        Block    behind    = Utilities.getBlockBehind(sign);
            
        // get shop
        final Shop        fShop     = scs.getShopHandler().getShop(behind);
        
        // actually... not a shop-block
        if (fShop == null) {
            return;
        }
        
        
        /*
         * Has to be called after the event,
         * because the Sign is not available
         * at this moment
         */
        scs.getServer().getScheduler().callSyncMethod(scs, new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                // call the event to format the sign
                updateSign(fShop);
                
                return null;
            }
        });
        
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onOwnerSetEvent (ShowCaseOwnerSetEvent event) {
        updateSign(event.getShop());
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPriceSetEvent (ShowCasePriceSetEvent event) {
        updateSign(event.getShop());
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onItemAddEvent (ShowCaseItemAddEvent event) {
        updateSign(event.getShop());
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onItemRemoveEvent (ShowCaseItemRemoveEvent event) {
        updateSign(event.getShop());
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerBuyEvent (ShowCasePlayerBuyEvent event) {
        updateSign(event.getShop());
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerSellEvent (ShowCasePlayerSellEvent event) {
        updateSign(event.getShop());
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerExchangeEvent (ShowCasePlayerExchangeEvent event) {
        updateSign(event.getShop());
    }
    
    /**
     * Updates all signs for the given Shop
     * @param shop    Shop to update the signs for
     */
    public void updateSign (Shop shop) {
        // format and update the sign
        for (Sign sign : getSigns(shop)) {
            formatSign(sign, shop);
            sign.update(true);
        }
    }
    
    
    
    /**
     * Formats the sign
     * @param sign Sign to format
     * @param shop Shop to gather the information from
     */
    public void formatSign (Sign sign, Shop shop) {
        String owner = shop.getOwnerName();

        if (owner == null && shop.getOwnerId() != null) {
            owner = shop.getOwnerId().toString();
        }

        sign.setLine(0, owner != null ? owner : "");
        sign.setLine(1, shop.getClass().getSimpleName());
        sign.setLine(2, shop.isUnlimited() ? Term.SIGN_UNLIMITED.get() : ""+shop.getAmount());
        sign.setLine(3, Term.SIGN_PRICE.get(""+shop.getPrice()));
    }
    
    
    /**
     * This method will check for Signs around the given Shop
     * It will check this every time and won't buffer any
     * information - since the Sign instance is not updated
     * by bukkit - so each call of this method will cost
     * a lot of performance (compared to others)
     * @return A List of Signs that surround this Shop
     */
    public List<Sign> getSigns (Shop shop) {
        // init lists
        List<Block> blocks    = new ArrayList<Block>();
        List<Sign>    signs    = new ArrayList<Sign>();
        
        // collect information
        Location    loc      = shop.getLocation();
        World       world    = shop.getWorld();
        int         x        = loc.getBlockX();
        int         y        = loc.getBlockY();
        int         z        = loc.getBlockZ();
        
        // the the possible for blocks around to the list
        blocks.add(world.getBlockAt(x+1, y, z  ));
        blocks.add(world.getBlockAt(x-1, y, z  ));
        blocks.add(world.getBlockAt(x,   y, z+1));
        blocks.add(world.getBlockAt(x,   y, z-1));
        
        // check if the possible blocks are signs
        for (Block block : blocks) {
            // is the block a Sign?
            if (block.getState() instanceof Sign) {
                Sign sign = (Sign)block.getState();
                
                // is the sign attached to me?
                if (Utilities.isShopBehind(sign, shop)) {
                    signs.add((Sign)block.getState());
                }
            }
        }
        
        return signs;
    }
}
