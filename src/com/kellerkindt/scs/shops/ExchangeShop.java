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

@SerializableAs(Properties.ALIAS_SHOP_EXCHANGE)
public class ExchangeShop extends Shop {
    
    public static final String KEY_EXCHANGE_ITEMSTACK    = "exchange.itemstack";
    public static final String KEY_EXCHANGE_AMOUNT        = "exchange.amount";
    
    private ItemStack        exItemStack;
    private int                exchangeAmount;    // amount to exchange items
    
    private ExchangeShop () {
        super();
    }    
    
    public ExchangeShop (UUID uuid, UUID owner, Location location, ItemStack itemStack, ItemStack exItemStack) {
        super(uuid, owner, location, itemStack);
        
        this.setExchangeItemStack    (exItemStack);
    }
    
    /**
     * @see com.kellerkindt.scs.shops.Shop#isActive()
     */
    @Override
    public boolean isActive() {
        return isUnlimited() || getAmount() > 0; // TODO check
    }
    
    /**
     * @see com.kellerkindt.scs.shops.Shop#serialize()
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        
        // add my value
        map.put(KEY_EXCHANGE_AMOUNT,     getExchangeAmount());
        map.put(KEY_EXCHANGE_ITEMSTACK, getExchangeItemStack());
        
        return map;
    }
    
    /**
     * @see ConfigurationSerializable
     */
    public static ExchangeShop deserialize (Map<String, Object> map) {
        ExchangeShop shop = new ExchangeShop();
        
        // just deserialize the common values
        shop.deserialize(map, Bukkit.getServer());
        
        shop.setExchangeAmount        ((Integer)    map.get(KEY_EXCHANGE_AMOUNT));
        shop.setExchangeItemStack    ((ItemStack)map.get(KEY_EXCHANGE_ITEMSTACK));
        
        return shop;
    }
    
    /**
     * @see com.kellerkindt.scs.shops.Shop#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode()
                +(exItemStack != null ? exItemStack.hashCode(): 0)
                +exchangeAmount;
    }
    
    /**
     * @return The amount of exchange-items
     */
    public int getExchangeAmount () {
        return exchangeAmount;
    }
    
    /**
     * Sets the amount of the exchange-items
     * @param value
     */
    public void setExchangeAmount (int value) {
        this.exchangeAmount = value;
    }

    /**
     * @return The ItemStack of the exchange item
     */
    public ItemStack getExchangeItemStack () {
        return exItemStack;
    }
    
    /**
     * Sets the exchange-ItemStack
     * @param ex
     */
    public void setExchangeItemStack (ItemStack ex) {
        this.exItemStack = ex;
    }
}

