/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-08-16 22:43 +02 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kellerkindt.scs.shops;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.internals.NamedUUID;
import com.kellerkindt.scs.utilities.MaterialNames;
import com.kellerkindt.scs.utilities.Term;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@SerializableAs(Properties.ALIAS_SHOP_DISPLAY)
public class DisplayShop extends Shop {
    
    private DisplayShop () {
        super();
    }    
    
    public DisplayShop (ShowCaseStandalone scs, UUID id, NamedUUID owner, Location location, ItemStack itemStack) {
        super(scs, id, owner, location, itemStack);
    }
    
    /**
     * @see com.kellerkindt.scs.shops.Shop#isActive()
     */
    @Override
    public boolean isActive() {
        return true;
    }


    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        String       name = scs.getPlayerNameOrNull(owner);

        list.add(Term.INFO_SHOP_DISPLAY.get(
                MaterialNames.getItemName(getItemStack()),
                null,
                name != null ? Term.INFO_SHOP_BY_PLAYER.get(name) : null
        ));

        getEnchantmentDescription(list, getItemStack());

        return list;
    }

    @Override
    public String getHoverText() {
        return getHoverText(Term.SHOP_ITEM_CUSTOM_NAME_TYPE_DISPLAY);
    }

    /**
     * @see ConfigurationSerializable
     */
    public static DisplayShop deserialize (Map<String, Object> map) {
        DisplayShop shop = new DisplayShop();
        
        // just deserialize the common values
        shop.deserialize(map, Bukkit.getServer());
        
        return shop;
    }
}
