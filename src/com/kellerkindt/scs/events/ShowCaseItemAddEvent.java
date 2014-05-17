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

import com.kellerkindt.scs.shops.Shop;

/**
 * This will cause in the executer to add the amount to the
 * shop, and in the verifier to remove it from the player
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCaseItemAddEvent extends ShowCaseItemEvent {
	
	public ShowCaseItemAddEvent (Player player, Shop shop, int amount, ItemStack stack) {
		super (player, shop, amount, stack);
	}
	
}
