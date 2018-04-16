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

import com.kellerkindt.scs.shops.Shop;

/**
 *
 * @author kellerkindt <michael at kellerkindt.com>
 */
public abstract class ShowCaseItemEvent extends ShowCaseShopEvent {
    
    private int            amount            = 0;
    private int            amountBefore    = 0;
    private ItemStack    stack            = null;
    
    public ShowCaseItemEvent (Player player, Shop shop, int amount, ItemStack stack) {
        super (player, shop);
        
        this.amount            = amount;
        this.amountBefore    = shop.getAmount();
        this.stack            = stack;
    }
    
    /**
     * @return The ItemStack to change the amount of
     */
    public ItemStack getItemStack () {
        return stack;
    }
    
    /**
     * @return The amount of the shop at the creation of this event
     */
    public int getAmountBefore () {
        return amountBefore;
    }
    
    /**
     * @return The amount to add
     */
    public int getAmount () {
        return amount;
    }
    
    /**
     * @param amount The amount to add
     */
    public void setAmount (int amount) {
        this.amount    = amount;
    }

}
