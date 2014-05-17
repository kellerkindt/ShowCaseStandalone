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

import java.util.Iterator;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;

/**
* Copyright (C) 2011 Kellerkindt <michael at kellerkindt.com>
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

public class EntityListener implements Listener {
    private ShowCaseStandalone scs; 
    
    public EntityListener(ShowCaseStandalone scs) {
        this.scs = scs;
    }
    

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
    	if(event.isCancelled()) {
    		return;
    	}
    	
    	// save the ShowCase?
    	if (scs.getConfiguration().isCancelingExplosions()) {
    	
    		Iterator<Block> blocks 	= event.blockList().iterator();
    		Block			block	= null;
    		
    		while (blocks.hasNext()) {
    			
    			// next block
    			block = blocks.next();
    			
    			// is shop block?s
    			if (scs.getShopHandler().isShopBlock(block)) {
    				// remove
    				blocks.remove();
    			}
    		}
    	
    	
    	// delete the ShowCase
    	} else {
    		
    		// iterate through
    		for (Block block : event.blockList()) {
    			
    			// get the shop
    			Shop shop = scs.getShopHandler().getShop(block);
    			
    			// is valid?
    			if (shop != null) {
    				// delete
    				scs.getShopHandler().removeShop(shop);
    				
    				// message to the owner
    				scs.sendMessageToOwner(shop, Term.MESSAGE_EXPLODED.get(shop.getItemStack().getItemMeta().getDisplayName()));
    			}
    		}
    		
    	}
    }
}
