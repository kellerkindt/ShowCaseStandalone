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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseDeleteEvent;
import com.kellerkindt.scs.events.ShowCaseInteractEvent;
import com.kellerkindt.scs.events.ShowCaseItemAddEvent;
import com.kellerkindt.scs.events.ShowCaseItemRemoveEvent;
import com.kellerkindt.scs.events.ShowCaseRemoveEvent;
import com.kellerkindt.scs.events.ShowCaseShopEvent;
import com.kellerkindt.scs.shops.DisplayShop;
import com.kellerkindt.scs.shops.ExchangeShop;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.ItemStackUtilities;

public class InventoryListener implements Listener {
    
    public static final int MAX_INVENTORY_SIZE    = 6*9;
    
    private ShowCaseStandalone   scs;
    private Map<Shop, Inventory> inventories    = new HashMap<Shop, Inventory>();
    private Map<Shop, int[]>     amountOpened   = new HashMap<Shop, int[]>();
    
    public InventoryListener (ShowCaseStandalone scs) {
        this.scs    = scs;
    }
    
    
    @EventHandler(ignoreCancelled=true)
    public void onItemRemoveEvent (ShowCaseItemRemoveEvent scire) {

        // cancel the event if the inventory is opened
        if (scire.verify() && hasShopInventoryViewers(scire.getShop())) {
            scire.setCancelled(true);
        }
    }
    
    
    @EventHandler(ignoreCancelled=true)
    public void onItemAddEvent (ShowCaseItemAddEvent sciae) {
        
        // cancel the event if the inventory is opened
        if (sciae.verify() && hasShopInventoryViewers(sciae.getShop())) {
            sciae.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onShopDeleteEvent (ShowCaseDeleteEvent scde) {
        Shop      shop      = scde.getShop();
        Inventory inventory = inventories.get(shop);
        
        if (inventory != null) {
            closeInventoryForShop(shop);
            updateShop(inventory, scde.getPlayer(), shop);
            
            inventories .remove(shop);
            amountOpened.remove(shop);
        }
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onShopRemoveEvent (ShowCaseRemoveEvent scre) {
        Shop      shop      = scre.getShop();
        Inventory inventory = inventories.get(shop);
        
        if (inventory != null) {
            closeInventoryForShop(shop);
            updateShop(inventory, scre.getPlayer(), shop);
            
            inventories .remove(shop);
            amountOpened.remove(shop);
        }
    }
    
    @EventHandler(ignoreCancelled=true)    // TODO
    public void onShopInteraction (final ShowCaseInteractEvent scie) {
        Shop shop    = scie.getShop();
        
        // also for display showcases?
        if (shop instanceof DisplayShop && !scs.getConfiguration().isDisplayShopUsingStorage()) {
            return;
        }
        
        // show the inventory to the owner and members
        if (shop.isOwnerOrMember(scie.getPlayer().getUniqueId())) {
            // get the inventory
            Inventory inventory = getOrCreateInventory(shop);
            
            if (inventory.getViewers().size() == 0) {
                // update the inventory
                updateInventory(inventory, shop);
            }
            
            // open the inventory
            scie.getPlayer().openInventory(inventory);
            scie.consume(this);
        }
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onInventoryClose (InventoryCloseEvent event) {
        
        Inventory    inventory    = event.getInventory();
        Shop        shop        = null;
        
        
        
        if (inventory.getHolder() instanceof ShopHolder) {
            
            // check for too many viewers
            if (inventory.getViewers().size() <= 1) {    // because the current player is also be counted

                // get the shop
                shop = ((ShopHolder)inventory.getHolder()).getShop();
                
                // update the inventory
                updateShop(inventory, (Player)event.getPlayer(), shop);
            }
        }
    }
    
    
    /**
     * Closes the inventory for all viewers from
     * the given Shop
     * @param shop    Inventory holder
     */
    private void closeInventoryForShop (Shop shop) {
        Inventory inventory = inventories.get(shop);
        
        // close the inventory for them
        for (HumanEntity entity : inventory.getViewers()) {
            entity.closeInventory();
        }
    }
    
    /**
     * @param shop Shop to check the inventory from
     * @return Whether the Inventory of the given Shop has viewers
     */
    private boolean hasShopInventoryViewers (Shop shop) {
        if (shop == null) {
            return false;
        }
        
        Inventory inventory = inventories.get(shop);
        
        if (inventory != null) {
            return (inventory.getViewers().size() > 0);
        }
        
        return false;
    }
    
    /**
     * Updates the shop with the counted items in the inventory
     * @param inventory    Inventory to count items from
     * @param shop        Shop to set the counted amount
     */
    private void updateShop (Inventory inventory, Player player, Shop shop) {        
        if (shop.isUnlimited()) {
            // no changes on an unlimited shop
            return;
        }
        
        // no value set? -> do nothing
        if (!amountOpened.containsKey(shop)) {
            return;
        }
        
        // get and remove the value
        int before[]     = amountOpened.remove(shop);
        
        // normal items
        countAndAddItemStack(inventory, player, shop, shop.getItemStack(), before[0]);
        
        // exchange items
        if (shop instanceof ExchangeShop) {
            countAndAddItemStack(inventory, player, shop, ((ExchangeShop)shop).getExchangeItemStack(), before[1]);
        }
    }
    
    /**
     * Counts and adds or removes item changes
     * @param inventory
     * @param player
     * @param shop
     * @param stack
     * @param before
     */
    private void countAndAddItemStack (Inventory inventory, Player player, Shop shop, ItemStack stack, int before) {
        int            counted        = 0;
        int            toAdd        = 0;
        
        // count the normal items
        counted = ItemStackUtilities.countCompatibleItemStacks(inventory, stack, scs.compareItemMeta(stack));
        
        // how many to add
        toAdd = (counted - before);
        
        ShowCaseShopEvent event = null;
        
        // call the event
        if (toAdd < 0) {
            // set event
            event = new ShowCaseItemRemoveEvent(player, shop, toAdd*-1, stack).setVerify(false);
            
        } else {
            // set event
            event = new ShowCaseItemAddEvent(player, shop, toAdd, stack).setVerify(false);
        }
        
        // call the event
        scs.callShowCaseEvent(event, null);
    }
    
    
    /**
     * Updates the inventory amount
     * @param inventory    Inventory to update
     * @param shop        Shop to gather the information from
     */
    private void updateInventory (Inventory inventory, Shop shop) {
        // first clear
        inventory.clear();
        
        ExchangeShop exchange = shop instanceof ExchangeShop ? (ExchangeShop)shop : null;
        
        // limit by inventory size
        int     amount[]    = new int[2];
        amount[0]        = (exchange != null ? (MAX_INVENTORY_SIZE / 2) : MAX_INVENTORY_SIZE) * shop.getItemStack().getMaxStackSize();
        amount[1]        = (exchange != null ? (MAX_INVENTORY_SIZE / 2) * exchange.getExchangeItemStack().getMaxStackSize() : 0);
        
        
        
        // limit amount
        if (amount[0] > shop.getAmount() && !shop.isUnlimited()) {
            amount[0] = shop.getAmount();
        }
        
        // limit exchange amount
        if (exchange != null && amount[1] > exchange.getExchangeAmount() && !shop.isUnlimited()) {
            amount[1] = exchange.getExchangeAmount();
        }
        
        
        
        
        // add the normal items
        amount[0] = ItemStackUtilities.addToInventory(inventory, shop.getItemStack(), amount[0]);
        
        // add the exchange items
        if (exchange != null) {
            // add the exchange items
            amount[1] = ItemStackUtilities.addToInventory(inventory, exchange.getExchangeItemStack(), amount[1]);
        }
        

        // add the start amount
        amountOpened.put(shop, amount);
    }
    
    /**
     * @param shop Shop to create or get the inventory for
     * @return The inventory for the given Shop
     */
    private Inventory getOrCreateInventory (final Shop shop) {
        Inventory inventory = inventories.get(shop);
        
        if (inventory != null) {
            return inventory;
            
        } else {
            
            // create the inventory
            inventory = scs.getServer().createInventory(new ShopHolder(shop), MAX_INVENTORY_SIZE, shop.getClass().getSimpleName());
            
            inventories    .put(shop,         inventory);
            return inventory;
        }
    }
    
    /**
    * InventoryHolder implementation
    * @author kellerkindt <michael at kellerkindt.com>
    */
    private class ShopHolder implements InventoryHolder {

        private Shop shop;
        
        public ShopHolder (Shop shop) {
            this.shop    = shop;
        }
        
        /**
         * @return The shop of this ShopHolder
         */
        public Shop getShop () {
            return shop;
        }
        
        @Override
        public Inventory getInventory() {
            return inventories.get(shop);
        }
        
    }
}
