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

import com.kellerkindt.scs.shops.Shop;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.kellerkindt.scs.ShowCaseStandalone;

public class WorldListener implements Listener {
    private ShowCaseStandalone scs;
    
    public WorldListener (ShowCaseStandalone scs) {
        this.scs    = scs;

        for (World world : scs.getServer().getWorlds()) {
            for (Chunk k : world.getLoadedChunks()) {
                scs.getShopHandler().showShopsFor(k);
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
    public void onChunkLoad(ChunkLoadEvent event) {
        scs.getShopHandler().showShopsFor(event.getChunk());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        scs.getShopHandler().hideShopsFor(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDespawnEvent(ItemDespawnEvent event) {
        final Shop shop = scs.getShopHandler().getShop(event.getEntity());

        if (shop != null && shop.isVisible()) {
            event.setCancelled(true);

            // System.out.println("canceled ItemDespawnEvent");

            scs.getServer().getScheduler().scheduleSyncDelayedTask(scs, new Runnable() {
                @Override
                public void run() {
                    /*
                     * since canceling the event does not guarantee the further existence of the Item,
                     * recheck the shop show state as soon as this event has ended
                     */
                    scs.getShopHandler().recheckShopShowState(shop);
                }
            });
        }
    }
}
