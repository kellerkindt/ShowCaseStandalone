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

import com.kellerkindt.scs.exceptions.InsufficientPermissionException;
import com.kellerkindt.scs.utilities.Term;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseCreateEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;

public class WorldGuardListener implements Listener {

    protected ShowCaseStandalone scs;
    protected WorldGuardPlugin   worldGuard;
    
    public WorldGuardListener (ShowCaseStandalone scs, Plugin wGuard) {
        this.scs = scs;
        
        if (wGuard instanceof WorldGuardPlugin)
            worldGuard    = (WorldGuardPlugin)wGuard;
        else
            throw new ClassCastException("Given Plugin is not WorldGuard");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onShopCreate (ShowCaseCreateEvent event) {
        if (!event.verify()) {
            // nothing to do
            return;
        }

        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Entered ShowCaseExecutingListener::onShowCaseCreateEvent");
        }

        // TODO update WG integration
        Location location = event.getShop().getLocation();
        Player   player   = event.getPlayer();
        
        GlobalRegionManager manager   = worldGuard.getGlobalRegionManager();
        boolean             isAllowed = manager.canBuild(player, location);
        
        if (!isAllowed) {
            event.setCancelled(true);
            event.setCause(new InsufficientPermissionException(
                    Term.ERROR_INSUFFICIENT_PERMISSION_REGION.get()
            ));

            if (scs.getConfiguration().isDebuggingShopCreation()) {
                scs.getLogger().info("Declined cause player is not allowed to build here");
            }
        }

        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Leaving ShowCaseExecutingListener::onShowCaseCreateEvent");
        }
    }
}
