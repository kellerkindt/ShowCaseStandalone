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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.internals.NamedUUID;
import com.kellerkindt.scs.utilities.MaterialNames;
import com.kellerkindt.scs.utilities.Term;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import com.kellerkindt.scs.Properties;

/**
 * @author Kellerkindt
 * This class represents the buy-showcase
 */
@SerializableAs(Properties.ALIAS_SHOP_BUY)
public class BuyShop<T extends BuyShop<?>> extends Shop<T> {

    // --- for serialization and deserialization ---
    public static final String KEY_MAXAMOUNT = "buy.maxamount";
    // ---------------------------------------------
    
    protected int maxAmount = 0;
    
    private BuyShop () {
        super();
    }    
    
    public BuyShop (ShowCaseStandalone scs, UUID id, NamedUUID owner, Location location, ItemStack itemStack) {
        super(scs, id, owner, location, itemStack);
    }


    @Override
    public boolean isActive() {
        return isUnlimited() || getAmount() <= getMaxAmount();
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<String>();
        Term         term = isUnlimited() ? Term.INFO_SHOP_BUY_UNLIMITED : Term.INFO_SHOP_BUY;
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

    /**
     * @param amount The new max amount to buy for this {@link BuyShop}
     * @return itself
     */
    public T setMaxAmount (final int amount) {
        return setChanged(
                this.maxAmount != amount,
                new Runnable() {
                    @Override
                    public void run() {
                        BuyShop.this.maxAmount = amount;
                    }
                }
        );
    }
    
    /**
     * @return The max amount to buy for this {@link BuyShop}
     */
    public int getMaxAmount () {
        return maxAmount;
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        
        // add my value
        map.put(KEY_MAXAMOUNT, maxAmount);
        
        return map;
    }
    
    /**
     * @see ConfigurationSerializable
     */
    public static BuyShop deserialize (Map<String, Object> map) {
        BuyShop shop = new BuyShop();
        
        // just deserialize the common values
        shop.deserialize(map, Bukkit.getServer());

        // load this values
        shop.maxAmount = (Integer)map.get(KEY_MAXAMOUNT);
        
        return shop;
    }
}
