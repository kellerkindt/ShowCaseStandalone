/**
* ShowCaseStandalone
* Copyright (C) 2014 Kellerkindt <copyright at kellerkindt.com>
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
package com.kellerkindt.scs;

import org.bukkit.command.CommandSender;

import com.kellerkindt.scs.events.ShowCaseEvent;
import com.kellerkindt.scs.shops.Shop;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class EventShopManipulator implements ShopManipulator {

	private CommandSender		sender;
	private ShowCaseEvent 		event;
	private ShowCaseStandalone	scs;
	private boolean				requiresValid;

	
	public EventShopManipulator (ShowCaseStandalone scs, CommandSender sender) {
		this(scs, sender, null);
	}
	
	public EventShopManipulator (ShowCaseStandalone scs, CommandSender sender, boolean reqValid) {
		this(scs, sender, null, reqValid);
	}
	
	public EventShopManipulator (ShowCaseStandalone scs, CommandSender sender, ShowCaseEvent event) {
		this(scs, sender, event, true);
	}
	
	public EventShopManipulator (ShowCaseStandalone scs, CommandSender sender, ShowCaseEvent event, boolean reqValid) {
		this.scs			= scs;
		this.sender			= sender;
		this.event			= event;
		this.requiresValid	= reqValid;
	}
	
	/**
	 * @param shop	{@link Shop} to manipulate
	 * @return The {@link ShowCaseEvent} to execute on a manipulate request
	 */
	public ShowCaseEvent getEvent (Shop shop) {
		return event;
	}
	
	@Override
	public void manipulate(Shop shop) {
		// get the event to call
		ShowCaseEvent event = getEvent(shop);
		
		// call it
		scs.callShowCaseEvent(event, sender);
		
	}
	
	@Override
	public boolean requiresValidShop() {
		return requiresValid;
	}
}
