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

import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import com.kellerkindt.scs.ShowCaseStandalone;


/**
*
* @author michael <michael at kellerkindt.com>
*/
public class HopperListener implements Listener {
    
    private ShowCaseStandalone scs;
    
    public HopperListener(ShowCaseStandalone scs) {
        this.scs = scs;
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onInventoryPickupEvent (InventoryPickupItemEvent event) {
        // cancel it, if it is an SCS item and the holder is a hopper
        if (scs.getShopHandler().isShopItem(event.getItem()) && event.getInventory().getHolder() instanceof Hopper) {
            event.setCancelled(true);
        }
    }
}
