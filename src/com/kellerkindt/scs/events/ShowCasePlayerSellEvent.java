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
import org.bukkit.inventory.ItemStack;

import com.kellerkindt.scs.shops.BuyShop;

/**
 * This event is used, if a player wants to
 * sell something to a shop
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCasePlayerSellEvent extends ShowCaseShopEvent {

	private int			quantity;
	
	public ShowCasePlayerSellEvent (Player player, BuyShop buyShop, int quantity) {
		super(player, buyShop);
		
		this.quantity	= quantity;
	}
	
	/**
	 * @return The ItemStack that the player wants to sell
	 */
	public ItemStack getItemStack () {
		return getShop().getItemStack();
	}
	
	/**
	 * @return The amount of Items the player wants to sell
	 */
	public int getQuantity () {
		return quantity;
	}
	
	/**
	 * @param quantity The new quantity
	 */
	public void setQuantity (int quantity) {
		this.quantity	= quantity;
	}
	
	@Override
	public BuyShop getShop () {
		return (BuyShop)super.getShop();
	}
}