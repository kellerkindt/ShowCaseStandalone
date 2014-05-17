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
package com.kellerkindt.scs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.narrowtux.dropchest.api.DropChestSuckEvent;

/**
* @author Sorklin <sorklin at gmail.com>
*/
public class DropChestListener implements Listener {
    private ShowCaseStandalone scs;
    
    public DropChestListener(ShowCaseStandalone instance) throws Exception {
        this.scs = instance;
    }

    @EventHandler
    public void onDropChestSuck(DropChestSuckEvent event) {
        if(scs.getShopHandler().isShopItem(event.getItem())){
            event.setCancelled(true);
        }   
    }
}
