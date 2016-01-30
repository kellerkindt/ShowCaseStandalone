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
import com.palmergames.bukkit.towny.object.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseCreateEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

import java.util.Date;

public class TownyListener implements Listener {
    
    private ShowCaseStandalone    scs;
    
    public TownyListener (ShowCaseStandalone scs) {
        this.scs = scs;
    }

    @EventHandler (priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onShowCaseCreateEvent (ShowCaseCreateEvent event) {
        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Entered TownyListener::onShowCaseCreateEvent, cancelled="+event.isCancelled());
        }

        Location location = event.getShop().getLocation();
        Player   player   = event.getPlayer();
        
        // http://code.google.com/a/eclipselabs.org/p/towny/issues/detail?id=1400
        if (TownyUniverse.getTownBlock(location) != null && !TownyUniverse.getTownBlock(location).hasTown()) {
            return;
        }

        if (!scs.getConfiguration().isTownyAllowingInWilderness()) {
            if (isWilderness(location)) {
                event.setCancelled(true);
                event.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_REGION.get()));

                if (scs.getConfiguration().isDebuggingShopCreation()) {
                    scs.getLogger().info("Declined cause not allowed in wilderness");
                }
            }
        }
        
        try {
            if (scs.getConfiguration().isTownyNeedingToBeOwner()) {
                if (!isPlotOwner(player, location)) {
                    event.setCancelled(true);
                    event.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_REGION.get()));

                    if (scs.getConfiguration().isDebuggingShopCreation()) {
                        scs.getLogger().info("Declined cause player is not owner of plot");
                    }
                }
            }
            
        } catch (NotRegisteredException nre) {
            // TODO decide on what to do on default, inside the calling method (default return true or false)
            ShowCaseStandalone.getWarnings().put(new Date(), nre.toString());
        }
        
        try {
            if (scs.getConfiguration().isTownyNeedingResident()) {
                if (!hasResident(player, location)) {
                    event.setCancelled(true);
                    event.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_REGION.get()));

                    if (scs.getConfiguration().isDebuggingShopCreation()) {
                        scs.getLogger().info("Declined cause player is not resident");
                    }
                }
            }
            
        } catch (NotRegisteredException nre) {
            // TODO decide on what to do on default, inside the calling method (default return true or false)
            ShowCaseStandalone.getWarnings().put(new Date(), nre.toString());
        }

        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Leaving TownyListener::onShowCaseCreateEvent, cancelled="+event.isCancelled());
        }
    }
    
    
    /**
     * @param location The {@link Location} to check whether is wilderness
     * @return true if the given location is wilderness
     */
    public boolean isWilderness (Location location) {
        return TownyUniverse.isWilderness(location.getBlock());
    }
    
    /**
     * @param player {@link Player} that requires to be owner
     * @param location {@link Location} to check ownership for
     * @return true if the given player is owner in the given plot
     * @throws NotRegisteredException 
     */
    public boolean isPlotOwner (Player player, Location location) throws NotRegisteredException {
        TownBlock       block = TownyUniverse.getTownBlock(location);
        TownBlockOwner  owner = TownyUniverse.getDataSource().getResident(player.getName());
        return block != null && owner != null && block.isOwner(owner);
    }

    /**
     * @param player {@link Player} that requires to be resident
     * @param location {@link Location} to check for
     * @return    true if the given player has a resident in the town 
     * @throws NotRegisteredException 
     */
    public boolean hasResident (Player player, Location location) throws NotRegisteredException {
        TownBlock block = TownyUniverse.getTownBlock(location);
        Town      town  = block != null ? block.getTown() : null;

        return town != null && town.hasResident(player.getName());
    }
    
    /**
     * @param location {@link Location} to check for
     * @return    true if the given block is in a shop plot
     */
    public boolean isInsideShopPlot (Location location) {
        TownBlock block = TownyUniverse.getTownBlock(location);

        return block != null  && block.getType() == TownBlockType.COMMERCIAL;
    }
}
