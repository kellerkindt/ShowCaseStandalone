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

import com.kellerkindt.scs.Properties;

@SerializableAs(Properties.ALIAS_SHOP_SELL)
public class SellShop extends Shop {
    
    private SellShop () {
        super();
    }
    
    public SellShop (UUID uuid, UUID owner, Location location, ItemStack itemStack) {
        super(uuid, owner, location, itemStack);
    }

    /**
     * @see com.kellerkindt.scs.shops.Shop#isActive()
     */
    @Override
    public boolean isActive() {
        return isUnlimited() || getAmount() > 0;
    }
    
    
    /**
     * @see ConfigurationSerializable
     */
    public static SellShop deserialize (Map<String, Object> map) {
        SellShop shop = new SellShop();
        
        // just deserialize the common values
        shop.deserialize(map, Bukkit.getServer());
        
        return shop;
    }

}
