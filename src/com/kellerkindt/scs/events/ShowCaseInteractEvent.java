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
 * This Event is called, if any interaction between the Player and
 * a ShowCase Shop should happen (e.g Create, Buy, Sale..)
 * This event is not to geht more information about a shop
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCaseInteractEvent extends ShowCaseShopEvent {

	private boolean	rightClicked	= true;
	
	public ShowCaseInteractEvent (Player player, Shop shop, boolean rightClicked) {
		super(player, shop);
	}
	
	/**
	 * @return Whether the event was caused by a right click
	 */
	public boolean hasRightClicked () {
		return rightClicked;
	}
	
	/**
	 * @return Whether the given 
	 */
	public boolean hasLeftClicked () {
		return !hasRightClicked();
	}
	
}
