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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SerializableAs(Properties.ALIAS_SHOP_SELL)
public class SellShop extends Shop {
    
    private SellShop () {
        super();
    }
    
    public SellShop (ShowCaseStandalone scs, UUID id, NamedUUID owner, Location location, ItemStack itemStack) {
        super(scs, id, owner, location, itemStack);
    }

    @Override
    public boolean isActive() {
        return isUnlimited() || getAmount() > 0;
    }


    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        Term         term = isUnlimited() ? Term.INFO_SHOP_SELL_UNLIMITED : Term.INFO_SHOP_SELL;
        String       name = scs.getPlayerNameOrNull(owner);

        list.add(term.get(
                MaterialNames.getItemName(getItemStack()),
                scs.getBalanceHandler().format(getPrice()),
                name != null   ? Term.INFO_SHOP_BY_PLAYER    .get(name)           : "",
                !isUnlimited() ? Term.INFO_SHOP_STOCK_CURRENT.get(""+getAmount()) : ""
        ));

        getEnchantmentDescription(list, getItemStack());

        return list;
    }

    @Override
    public String getHoverText() {
        return ChatColor.YELLOW+getHoverText(Term.SHOP_ITEM_CUSTOM_NAME_TYPE_SELL);
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
