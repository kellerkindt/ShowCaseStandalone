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
package com.kellerkindt.scs.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kellerkindt.scs.shops.SellShop;

/**
 * This event is used, if a player wants to
 * buy from a shop
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCasePlayerBuyEvent extends ShowCaseShopEvent {

    private int            quantity;
    private ItemStack    stack;
    
    public ShowCasePlayerBuyEvent (Player player, SellShop sellShop, int quantity) {
        super(player, sellShop);
        
        this.quantity    = quantity;
        this.stack        = sellShop.getItemStack();
    }
    
    /**
     * @return The ItemStack that the player wants to buy
     */
    public ItemStack getItemStack () {
        return stack;
    }
    
    /**
     * @param stack The ItemStack to add to the player
     */
    public void setItemStack (ItemStack stack) {
        this.stack = stack;
    }
    
    /**
     * @return The amount of Items the player wants to buy
     */
    public int getQuantity () {
        return quantity;
    }
    
    /**
     * @param quantity The new quantity
     */
    public void setQuantity (int quantity) {
        this.quantity    = quantity;
    }
    
    @Override
    public SellShop getShop () {
        return (SellShop)super.getShop();
    }
}
