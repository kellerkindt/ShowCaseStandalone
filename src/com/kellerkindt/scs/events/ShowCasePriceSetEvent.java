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
 *
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCasePriceSetEvent extends ShowCaseShopEvent {

	private double price;
	private double priceBefore;
	
	public ShowCasePriceSetEvent (Player player, Shop shop, double price) {
		super(player, shop);
		
		this.price			= price;
		this.priceBefore	= shop.getPrice();
	}
	
	/**
	 * @return The price at the moment the event was created
	 */
	public double getPriceBefore () {
		return priceBefore;
	}
	
	/**
	 * @return The new price to set
	 */
	public double getPrice () {
		return price;
	}
	
	/**
	 * @param price The new price to set
	 */
	public void setPrice (double price) {
		this.price = price;
	}
}
