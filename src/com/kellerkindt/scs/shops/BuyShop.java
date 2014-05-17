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
package com.kellerkindt.scs.shops;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import com.kellerkindt.scs.ShowCaseStandalone;

/**
 * @author Kellerkindt
 * This class represents the buy-showcase
 */
@SerializableAs(ShowCaseStandalone.ALIAS_SHOP_BUY)
public class BuyShop extends Shop {
	
	public static final String KEY_MAXAMOUNT = "buy.maxamount";
	
	private int maxAmount	= 0;
	
	private BuyShop () {
		super();
	}	
	
	public BuyShop (UUID uuid, UUID owner, Location location, ItemStack itemStack) {
		super(uuid, owner, location, itemStack);
	}
	
	/**
	 * @see com.kellerkindt.scs.shops.Shop#isActive()
	 */
	@Override
	public boolean isActive() {
		return isUnlimited() || getAmount() <= getMaxAmount();
	}

	/**
	 * The max amount to buy in this shop
	 * @param amount Max amount to set
	 */
	public void setMaxAmount (int amount) {
		this.maxAmount	= amount;
	}
	
	/**
	 * @return The max amount to buy of this shop
	 */
	public int getMaxAmount () {
		return maxAmount;
	}
	
	/**
	 * @see com.kellerkindt.scs.shops.Shop#serialize()
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
		// add my value
		map.put(KEY_MAXAMOUNT, getMaxAmount());
		
		return map;
	}
	
	/**
	 * @see ConfigurationSerializable
	 */
	public static BuyShop deserialize (Map<String, Object> map) {
		BuyShop shop = new BuyShop();
		
		// just deserialize the common values
		shop.deserialize(map, Bukkit.getServer());
		
		shop.setMaxAmount((Integer)map.get(KEY_MAXAMOUNT));
		
		return shop;
	}
	
	/**
	 * @see com.kellerkindt.scs.shops.Shop#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode()
				+maxAmount;
	}
}
