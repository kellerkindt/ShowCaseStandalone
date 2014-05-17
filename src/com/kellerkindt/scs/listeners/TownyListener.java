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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseCreateEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlockOwner;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyListener implements Listener {
	
	private ShowCaseStandalone	scs;
	
	public TownyListener (ShowCaseStandalone scs) {
		this.scs = scs;
	}

	@EventHandler (priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onShowCaseCreateEvent (ShowCaseCreateEvent event) {
		
		Location			location	= event.getShop().getLocation();
		Player				player		= event.getPlayer();
		
		// http://code.google.com/a/eclipselabs.org/p/towny/issues/detail?id=1400
		if (!TownyUniverse.getTownBlock(location).hasTown()) {
			return;
		}

		if (!scs.getConfiguration().isTownyAllowingInWilderness())
			if (isWilderness(location))
				event.setCancelled(true);
		
		try {
			if (scs.getConfiguration().isTownyNeedingToBeOwner())
				if (!isPlotOwner(player, location))
					event.setCancelled(true);
			
		} catch (NotRegisteredException nre) {}
		
		try {
			if (scs.getConfiguration().isTownyNeedingResident())
				if (!hasResident(player, location))
					event.setCancelled(true);
			
		} catch (NotRegisteredException nre) {}
		
	}
	
	
	/**
	 * @param location
	 * @return true if the given location is wilderness
	 */
	public boolean isWilderness (Location location) {
		return TownyUniverse.isWilderness(location.getBlock());
	}
	
	/**
	 * @param player
	 * @param location
	 * @return true if the given player is owner in the given plot
	 * @throws NotRegisteredException 
	 */
	public boolean isPlotOwner (Player player, Location location) throws NotRegisteredException {
		TownBlockOwner	owner	= TownyUniverse.getDataSource().getResident(player.getName());
		return TownyUniverse.getTownBlock(location).isOwner(owner);
	}
	
	/**
	 * @param player
	 * @param location
	 * @return	true if the given player has a resident in the town 
	 * @throws NotRegisteredException 
	 */
	public boolean hasResident (Player player, Location location) throws NotRegisteredException {
		return TownyUniverse.getTownBlock(location).getTown().hasResident(player.getName());
	}
	
	/**
	 * @param location
	 * @return	true if the given block is in a shop plot
	 */
	public boolean isInsideShopPlot (Location location) {
		return TownyUniverse.getTownBlock(location).getType() == TownBlockType.COMMERCIAL;
	}
}
