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

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.kellerkindt.scs.LocationSelector;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseEvent;
import com.kellerkindt.scs.events.ShowCaseInfoEvent;
import com.kellerkindt.scs.events.ShowCaseInteractEvent;
import com.kellerkindt.scs.shops.Shop;

public class PlayerListener implements Listener {
    
    private ShowCaseStandalone scs;

    public PlayerListener(ShowCaseStandalone instance) {
        scs = instance;
    }
    
        /*
     * Cancel pickup of a Item if the item is a shop Item
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem (PlayerPickupItemEvent pe) {
        if ( scs.getShopHandler().isShopItem(pe.getItem()) ) {
            pe.setCancelled(true);                
        }
    }
    
    /*
     * Let the player Interact with the shop
     * Lets keep the priority low, so we don't get cancelled when we're not doing anything.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onPlayerInteract (PlayerInteractEvent pie) {
        
        // Abort if action does not fit - saves power :)
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !pie.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        
        Action                action        = pie.getAction();
        Player                player        = pie.getPlayer();
        Block                block         = pie.getClickedBlock();

        LocationSelector    locSelector    = scs.removeLocationSelector(player);
        Shop                 shop        = scs.getShopHandler().getShop(block);
        ShowCaseEvent        event        = null;
        
        if (locSelector != null && action == Action.RIGHT_CLICK_BLOCK) {
            // a location has been selected
            locSelector.onLocationSelected(block.getLocation());
            
        } else if (shop != null && action == Action.RIGHT_CLICK_BLOCK) {
            // interact with the shop
            event = new ShowCaseInteractEvent(player, shop, true);
            
        } else if (shop != null && action == Action.LEFT_CLICK_BLOCK) {
            // info event
            event = new ShowCaseInfoEvent(player, shop);
            
            
        }
        
        if (event != null) {            
            // dispatch event
            scs.callShowCaseEvent(event, player);
            
            /*
             *  cancel the current event so nothing else does work with the interaction,
             *  if the ShowCaseEvent was not cancelled
             */
            if (!event.isCancelled()) {
                pie.setCancelled(true);
            }
        }
    }
}
