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

import com.kellerkindt.scs.shops.BuyShop;
import com.kellerkindt.scs.shops.Shop;

/**
 *
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCaseLimitEvent extends ShowCaseShopEvent {
    
    private int limit;
    private int limitBefore;

    public ShowCaseLimitEvent(Player player, Shop shop, int limit) {
        super(player, shop);
        
        this.limit            = limit;
        this.limitBefore    = shop instanceof BuyShop ? ((BuyShop)shop).getMaxAmount() : 0;
    }
    
    /**
     * @return The limit that was set before (if the given shop is not a BuyShop it is always 0)
     */
    public int getLimitBefore () {
        return limitBefore;
    }
    
    /**
     * @return The new limit to set
     */
    public int getLimit () {
        return limit;
    }

    /**
     * @param limit The new limit to set
     */
    public void setLimit (int limit) {
        this.limit    = limit;
    }
}
