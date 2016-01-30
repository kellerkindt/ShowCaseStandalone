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

import java.util.*;

@SerializableAs(Properties.ALIAS_SHOP_EXCHANGE)
public class ExchangeShop<T extends ExchangeShop<?>> extends Shop<T> {

    // --- for serialization and deserialization ---
    public static final String KEY_EXCHANGE_ITEMSTACK   = "exchange.itemstack";
    public static final String KEY_EXCHANGE_AMOUNT      = "exchange.amount";
    // ---------------------------------------------
    
    private ItemStack   exItemStack;
    private int         exchangeAmount;    // amount to exchange items
    
    private ExchangeShop () {
        super();
    }    
    
    public ExchangeShop (ShowCaseStandalone scs, UUID id, NamedUUID owner, Location location, ItemStack itemStack, ItemStack exItemStack) {
        super(scs, id, owner, location, itemStack);
        
        this.setExchangeItemStack    (exItemStack);
    }
    
    /**
     * @see com.kellerkindt.scs.shops.Shop#isActive()
     */
    @Override
    public boolean isActive() {
        return isUnlimited() || getAmount() > 0; // TODO check
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<String>();
        Term         term = isUnlimited() ? Term.INFO_SHOP_EXCHANGE_UNLIMITED : Term.INFO_SHOP_EXCHANGE;
        String       name = scs.getPlayerNameOrNull(owner);

        list.add(term.get(
                MaterialNames.getItemName(getItemStack()),
                ""+((int)getPrice()),
                name != null   ? Term.INFO_SHOP_BY_PLAYER    .get(name)           : "",
                !isUnlimited() ? Term.INFO_SHOP_STOCK_CURRENT.get(""+getAmount()) : "",
                MaterialNames.getItemName(getExchangeItemStack())
        ));

        getEnchantmentDescription(list, getItemStack());
        getEnchantmentDescription(list, getExchangeItemStack());

        return list;
    }

    /**
     * @see com.kellerkindt.scs.shops.Shop#serialize()
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        
        // add my value
        map.put(KEY_EXCHANGE_AMOUNT,    exchangeAmount);
        map.put(KEY_EXCHANGE_ITEMSTACK, exItemStack);
        
        return map;
    }
    
    /**
     * @see ConfigurationSerializable
     */
    public static ExchangeShop deserialize (Map<String, Object> map) {
        ExchangeShop shop = new ExchangeShop();
        
        // just deserialize the common values
        shop.deserialize(map, Bukkit.getServer());

        shop.exchangeAmount = ((Integer)  map.get(KEY_EXCHANGE_AMOUNT));
        shop.exItemStack    = ((ItemStack)map.get(KEY_EXCHANGE_ITEMSTACK));
        
        return shop;
    }

    
    /**
     * @return The amount of exchange-items
     */
    public int getExchangeAmount () {
        return exchangeAmount;
    }
    
    /**
     * @param amount The new amount of exchange-items
     * @return itself
     */
    public T setExchangeAmount (final int amount) {
        return setChanged(
                this.exchangeAmount != amount,
                new Runnable() {
                    @Override
                    public void run() {
                        ExchangeShop.this.exchangeAmount = amount;
                    }
                }
        );
    }

    /**
     * @return The {@link ItemStack} of the exchange items
     */
    public ItemStack getExchangeItemStack () {
        return exItemStack;
    }
    
    /**
     * @param itemStack The new {@link ItemStack} of the exchange items
     * @return itself
     */
    public T setExchangeItemStack (final ItemStack itemStack) {
        return setChanged(
                !Objects.equals(this.exItemStack, itemStack),
                new Runnable() {
                    @Override
                    public void run() {
                        ExchangeShop.this.exItemStack = itemStack;
                    }
                }
        );
    }
}

