/**
* ShowCaseStandalone
* Copyright (C) 2013 Kellerkindt <copyright at kellerkindt.com>
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
package com.kellerkindt.scs.events;

import org.bukkit.entity.Player;

import com.kellerkindt.scs.shops.Shop;

/**
 * ShowCaseStandalone base Event
 * @author kellerkindt <michael at kellerkindt.com>
 */
public abstract class ShowCaseShopEvent extends ShowCaseEvent {
	
	private Player		player		= null;
	private Shop		shop		= null;
	
	public ShowCaseShopEvent (Player player, Shop shop) {
		this.player	= player;
		this.shop	= shop;
	}
	
	/**
	 * @see com.kellerkindt.scs.events.ShowCaseEvent#setVerify(boolean)
	 */
	@Override
	public ShowCaseShopEvent setVerify(boolean verify) {
		return (ShowCaseShopEvent)super.setVerify(verify);
	}
	
	/**
	 * @return Whether a Player is involved in this event
	 */
	public boolean hasPlayer () {
		return getPlayer() != null;
	}
	
	/**
	 * @return Whether a Shop is involved in this event
	 */
	public boolean hasShop () {
		return getShop() != null;
	}
	
	/**
	 * @return The player that is involved in this event or null
	 */
	public Player getPlayer () {
		return player;
	}
	
	
	/**
	 * @return The Shop that is involved in this event or null
	 */
	public Shop getShop () {
		return shop;
	}
	
	/**
	 * @param shop {@link Shop} to handle in this event
	 */
	public void setShop (Shop shop) {
		this.shop = shop;
	}
	
}
