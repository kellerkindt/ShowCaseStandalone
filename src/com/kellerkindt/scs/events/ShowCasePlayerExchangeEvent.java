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

import com.kellerkindt.scs.shops.ExchangeShop;

/**
 * This event is used, when a player wants to
 * exchange items with a shop (ExchangeShop)
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCasePlayerExchangeEvent extends ShowCaseShopEvent {

    private int quantity;
    
    public ShowCasePlayerExchangeEvent (Player player, ExchangeShop shop, int quantity) {
        super(player, shop);
        
        this.quantity    = quantity;
    }
    
    /**
     * @return The amount of items to exchange
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
    public ExchangeShop getShop() {
        return (ExchangeShop)super.getShop();
    }
}
