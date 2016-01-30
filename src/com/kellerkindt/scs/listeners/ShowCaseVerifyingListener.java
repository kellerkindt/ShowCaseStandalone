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
package com.kellerkindt.scs.listeners;

import com.kellerkindt.scs.PriceRange;
import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.SCSConfiguration;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.*;
import com.kellerkindt.scs.exceptions.InsufficientPermissionException;
import com.kellerkindt.scs.interfaces.ShowCaseListener;
import com.kellerkindt.scs.shops.*;
import com.kellerkindt.scs.utilities.ItemStackUtilities;
import com.kellerkindt.scs.utilities.MaterialNames;
import com.kellerkindt.scs.utilities.Term;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.naming.InsufficientResourcesException;

/**
 * This class verifies whether the event has to be
 * cancelled or can be executed in the executive listener
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCaseVerifyingListener implements ShowCaseListener {

    private ShowCaseStandalone    scs;
    
    public ShowCaseVerifyingListener (ShowCaseStandalone scs) {
        this.scs        = scs;
    }
    

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseInfoEvent(com.kellerkindt.scs.events.ShowCaseInfoEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseInfoEvent(ShowCaseInfoEvent scie) {
        if (scie.verify() && !scs.canUse(scie.getPlayer())) {
            scie.setCancelled(true);
            scie.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION.get()));
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseInteractEvent(com.kellerkindt.scs.events.ShowCaseInteractEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseInteractEvent(ShowCaseInteractEvent scie) {
        
        /*
         *  Actually, do not check here.
         *  This event will call more events in
         *  the executer which then should be checked
         */
        
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseItemAddEvent(com.kellerkindt.scs.events.ShowCaseItemAddEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseItemAddEvent(ShowCaseItemAddEvent sciae) {
        if (sciae.verify()) {
            
            Player      player  = sciae.getPlayer();
            Shop        shop    = sciae.getShop();
            int         amount  = sciae.getAmount();
            
            Throwable   cause   = null;
            
            if (!scs.canManage(player, shop, false)) {
                // not enough permissions
                cause    = new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_ADD_ITEM.get());
                
            } else if (shop instanceof DisplayShop) {
                // you cannot add items to a display shop
                cause    = new RuntimeException(Term.ERROR_ADD_ITEMS_DISPlAY.get());
                
            } else if (shop.isUnlimited()) {
                // you cannot add items to an unlimited shop
                cause    = new RuntimeException(Term.ERROR_ADD_ITEMS_UNLIMITED.get());
            }
            
            if (cause == null) {
                // try to remove the amount of items from the players inventory
                amount = ItemStackUtilities.removeFromInventory(player.getInventory(), sciae.getItemStack(), amount, scs.compareItemMeta(sciae.getItemStack()));
                
                // set the event to the amount that was removed
                sciae.setAmount(amount);
                
            } else {
                // cancel the event
                sciae.setCancelled(true);
                sciae.setCause    (cause);
            }
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseMemberAddEvent(com.kellerkindt.scs.events.ShowCaseMemberAddEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseMemberAddEvent(ShowCaseMemberAddEvent scmae) {
        if (scmae.verify()) {
            
            Player  player  = scmae.getPlayer();
            Shop    shop    = scmae.getShop();
            
            // not an admin and not having the permission to manage the shop as owner
            if (!scs.isAdmin(player) && !scs.canManage(player, shop, true)) {
                // cancel the event
                scmae.setCancelled(true);
                scmae.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_ADD_MEMBER.get()));
            }
        }
    }
    
    private boolean handlePriceRangeEventCheck (ShowCaseShopEvent event, double price) {
        Material    material    = event.getShop().getItemStack().getType();
        PriceRange  range       = scs.getPriceRangeHandler().getRange(material, false);
        
        if (price > range.getMax() || price < range.getMin()) {
            event.setCancelled(true);
            event.setCause(new RuntimeException(
                    Term.ERROR_PRICE_NOT_IN_RANGE.get(
                            Double.toString( range.getMin() ),
                            Double.toString( range.getMax() == Double.MAX_VALUE ? Double.POSITIVE_INFINITY : range.getMax() )
                        )));
            return true;
        }
        
        return false;
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseCreateEvent(com.kellerkindt.scs.events.ShowCaseCreateEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseCreateEvent(ShowCaseCreateEvent scce) {
        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Entered ShowCaseVerifyingListener::onShowCaseCreateEvent, cancelled="+scce.isCancelled());
        }

        if (scce.verify()) {
            
            SCSConfiguration config = scs.getConfiguration();
            
            Player  player      = scce.getPlayer();
            Shop    shop        = scce.getShop();
            int     itemRemove  = shop.getAmount();
            
            double  cost        = scs.getCreatePrice      (shop.getClass());
            String  permCreate  = scs.getCreatePermission (shop.getClass());

            if (config.isDebuggingShopCreation()) {

            }
            
            if (shop instanceof DisplayShop) {
                // if you have the permission, you do not need a item to create the shop
                if (scs.isAdminOrHasPermission(player, Properties.PERMISSION_CREATE_DISPLAY_NOITEM)) {
                    itemRemove = 0;
                } else {
                    itemRemove = 1;
                }
            }
            
            // check unlimited permission
            if (shop.isUnlimited() && !scs.isAdminOrHasPermission(player, Properties.PERMISSION_CREATE_UNLIMITED)) {
                scce.setCancelled(true);
                scce.setCause(new InsufficientPermissionException(Term.ERROR_CREATE_UNLIMITED.get()));
            }
            
            //check the permissions
            else if (!scs.isAdminOrHasPermission(player, permCreate)) {
                scce.setCancelled(true);
                scce.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION.get()));
            }
            
            // check money
            else if (cost > 0 && !scs.getBalanceHandler().hasEnough(player.getUniqueId(), cost)) {
                scce.setCancelled(true);
                scce.setCause(new InsufficientResourcesException(Term.ERROR_INSUFFICIENT_MONEY_CREATE.get()));
            }
            
            // check if there is already a showcase
            else if (scs.getShopHandler().isShopBlock(shop.getBlock())) {

                scce.setCancelled(true);
                scce.setCause(new RuntimeException(Term.ERROR_ALREADY_SHOWCASE.get()));
            }
            
            // check whether the player has to many shops
            else if (config.getLimitationMaxAmountPerPlayer() >= 0 && scs.getShopHandler().getShopAmount(scce.getPlayer().getUniqueId()) >= config.getLimitationMaxAmountPerPlayer() && !scs.isAdmin(scce.getPlayer())) {
                scce.setCancelled(true);
                scce.setCause(new RuntimeException(Term.ERROR_SHOP_LIMIT_EXCEEDED.get()));
            }
            
            // check black list item
            else if ((shop instanceof SellShop   && (config.isSellItemListBlacklist()   == config.getSellItemListItemList() .contains(shop.getItemStack().getType().toString())))
                  || (shop instanceof BuyShop    && (config.isBuyItemListBlackList()    == config.getBuyItemListItemList () .contains(shop.getItemStack().getType().toString())))) {
                scce.setCancelled(true);
                scce.setCause(new InsufficientPermissionException(Term.BLACKLIST_ITEM.get()));
            }
            
            // check black list block
            else if (config.isBlockListBlacklist() == config.getBlockListBlocks().contains(shop.getBlock().getType().toString())) {
                scce.setCancelled(true);
                scce.setCause(new InsufficientPermissionException(Term.BLACKLIST_BLOCK.get()));
            }
            
            // check black list world
            else if (config.getWorldsBlacklisted().contains(shop.getWorld())) {
                scce.setCancelled(true);
                scce.setCause(new InsufficientPermissionException(Term.BLACKLIST_WORLD.get()));
            }
            
            else {
                // check the price
                handlePriceRangeEventCheck(scce, shop.getPrice());
            }
            
            // check the inventory
            if (!scce.isCancelled() && itemRemove > 0) {
                
                int removed = ItemStackUtilities.removeFromInventory(player.getInventory(), shop.getItemStack(), itemRemove, scs.compareItemMeta(shop.getItemStack()));
                
                if (removed > 0) {
                    // just to be sure, there is not item glitch here
                    shop.setAmount(removed);
                    
                } else {
                    // cancel the event
                    scce.setCancelled(true);
                    scce.setCause(new InsufficientResourcesException(Term.ERROR_INSUFFICIENT_ITEMS_CREATE.get()));
                }
            }
            
        }

        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Leaving ShowCaseVerifyingListener::onShowCaseCreateEvent, cancelled="+scce.isCancelled());
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseDeleteEvent(com.kellerkindt.scs.events.ShowCaseDeleteEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseDeleteEvent(ShowCaseDeleteEvent scde) {
        if (scde.verify()) {
            
            // only an admin can delete / destroy a shop
            if (!scs.isAdmin(scde.getPlayer())) {
                scde.setCancelled(true);
                scde.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_DESTROY.get()));
            }
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseItemRemoveEvent(com.kellerkindt.scs.events.ShowCaseItemRemoveEvent)
     * 
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseItemRemoveEvent(ShowCaseItemRemoveEvent scire) {
        if (scire.verify()) {
            
            Player player = scire.getPlayer();
            Shop   shop   = scire.getShop();
            int    amount = scire.getAmount();

            Throwable cause = null;
            
            if (!scs.canManage(player, shop, false)) {
                // not enough permissions
                cause    = new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_GET_ITEM.get());
                
            } else if (shop instanceof DisplayShop) {
                // you cannot add items to a display shop
                cause    = new RuntimeException(Term.ERROR_GET_DISPLAY.get());
                
            } else if (shop.isUnlimited()) {
                // you cannot add items to an unlimited shop
                cause    = new RuntimeException(Term.ERROR_ADD_ITEMS_UNLIMITED.get());
            }
            
            // limit by shop size
            else if (amount > shop.getAmount()) {
                amount = shop.getAmount();
            }
            
            if (cause == null) {
                // remove the items
                amount = ItemStackUtilities.addToInventory(player.getInventory(), scire.getItemStack(), amount);
                
                // set the amount to the removed amount
                scire.setAmount(amount);
                
            } else {
                // cancel the event
                scire.setCancelled(true);
                scire.setCause(cause);
            }
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseLimitEvent(com.kellerkindt.scs.events.ShowCaseLimitEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseLimitEvent(ShowCaseLimitEvent scle) {
        if (scle.verify() && !scs.canManage(scle.getPlayer(), scle.getShop(), false)) {
            scle.setCancelled(true);
            scle.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_SET_LIMIT.get()));
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseRemoveEvent(com.kellerkindt.scs.events.ShowCaseRemoveEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseRemoveEvent(ShowCaseRemoveEvent scre) {
        if (scre.verify()) {
        
            Player        player        = scre.getPlayer();
            Shop        shop        = scre.getShop();
            int            notAdded    = 0;
            
            Throwable    cause        = null;
            
            if (!scs.canManage(player, shop, true, Properties.PERMISSION_REMOVE)) {
                // not enough permissions
                cause = new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_REM_SHOWCASE.get());
                
            } else {
                
                if (shop instanceof DisplayShop) {
                    // give the item back, if you had to add an item
                    if (!scs.isAdminOrHasPermission(player, Properties.PERMISSION_CREATE_DISPLAY_NOITEM)) {
                        
                        // try to add
                        notAdded = ItemStackUtilities.addToInventory(player.getInventory(), shop.getItemStack(), 1);
                        
                        // has not been added?
                        if (notAdded > 0) {
                            
                            // not enough room in the inventory
                            cause = new InsufficientResourcesException(Term.ERROR_INSUFFICIENT_ROOM.get());
                        }
                    }
                }
                
                
                // only add items to inventory if it's not unlimited (rtainc's attempt)
                else if(!shop.isUnlimited()) {
                    
                    // add the items from the shop
                    notAdded = shop.getAmount() - ItemStackUtilities.addToInventory(player.getInventory(), shop.getItemStack(), shop.getAmount());
                
                    // add also the items of the exchange shop
                    if (shop instanceof ExchangeShop) {
                        ExchangeShop shopEx = (ExchangeShop)shop;
                        
                        notAdded += shopEx.getExchangeAmount() - ItemStackUtilities.addToInventory(player.getInventory(), shopEx.getExchangeItemStack(), shopEx.getExchangeAmount());
                    }
                    
                    if (notAdded > 0) {
                        /*
                         *  reduce the amount of this shop by the
                         *  added amount / set it to the not added amount
                         */
                        shop.setAmount(notAdded);
                        
                        // not enough room in the inventory
                        cause = new InsufficientResourcesException(Term.ERROR_INSUFFICIENT_ROOM.get());
                    }
                }
            }
            
            if (cause != null) {
                scre.setCancelled(true);
                scre.setCause(cause);
            }
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseMemberRemoveEvent(com.kellerkindt.scs.events.ShowCaseMemberRemoveEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseMemberRemoveEvent(ShowCaseMemberRemoveEvent scmre) {
        if (scmre.verify() && !scs.canManage(scmre.getPlayer(), scmre.getShop(), true)) {
            scmre.setCancelled(true);
            scmre.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_REM_MEMBER.get()));
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCaseOwnerSetEvent(com.kellerkindt.scs.events.ShowCaseOwnerSetEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCaseOwnerSetEvent(ShowCaseOwnerSetEvent scose) {
        if (scose.verify() && !scs.isAdmin(scose.getPlayer())) {
            scose.setCancelled(true);
            scose.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_SET_OWNER.get()));
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCasePriceSetEvent(com.kellerkindt.scs.events.ShowCasePriceSetEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCasePriceSetEvent(ShowCasePriceSetEvent scpse) {
        if (scpse.verify()) {
            
            if (!scs.canManage(scpse.getPlayer(), scpse.getShop(), false)) {
                scpse.setCancelled(true);
                scpse.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION_SET_PRICE.get()));
                
            } else {    
                // also verify the price
                handlePriceRangeEventCheck(scpse, scpse.getPrice());
            }
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCasePlayerBuyEvent(com.kellerkindt.scs.events.ShowCasePlayerBuyEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCasePlayerBuyEvent(ShowCasePlayerBuyEvent scpbe) {
        if (scpbe.verify()) {
            
            Player      player  = scpbe.getPlayer();
            int         amount  = scpbe.getQuantity();
            SellShop    shop    = scpbe.getShop();
            double      price   = shop.getPrice();
            Throwable   cause   = null;

    
            // fix amount
            if (amount > shop.getAmount() && !shop.isUnlimited()) {
                scpbe.setQuantity(shop.getAmount());
                amount = scpbe.getQuantity();
            }
            
            // insufficient permission
            if (!scs.canUse(player)) {
                cause = new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION.get());
                
            }
            
            // insufficient money
            else if (!scs.getBalanceHandler().hasEnough(player.getUniqueId(), amount*price)) {
                cause = new InsufficientResourcesException(Term.ERROR_INSUFFICIENT_MONEY_YOU.get());
            }
    
            if (cause != null) {
                scpbe.setCancelled(true);
                scpbe.setCause(cause);
            }
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCasePlayerSellEvent(com.kellerkindt.scs.events.ShowCasePlayerSellEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCasePlayerSellEvent(ShowCasePlayerSellEvent scpse) {
        if (scpse.verify()) {
            Player      player  = scpse.getPlayer();
            int         amount  = scpse.getQuantity();
            BuyShop     shop    = scpse.getShop();
            double      price   = shop.getPrice();
            Throwable   cause   = null;

            // fix amount
            if (amount > (shop.getMaxAmount()-shop.getAmount()) && !shop.isUnlimited()) {
                scpse.setQuantity(shop.getMaxAmount()-shop.getAmount());
                amount = scpse.getQuantity();
            }
            
            // insufficient permission
            if (!scs.canUse(player)) {
                cause = new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION.get());
                
            }
            
            // insufficient money
            else if (shop.getOwnerId() != null && !scs.getBalanceHandler().hasEnough(shop.getOwnerId(), amount*price)) {
                cause = new InsufficientResourcesException(Term.ERROR_INSUFFICIENT_MONEY_COSTUMER.get());
            }
    
            if (cause != null) {
                scpse.setCancelled(true);
                scpse.setCause(cause);
            }
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCasePlayerExchangeEvent(com.kellerkindt.scs.events.ShowCasePlayerExchangeEvent)
     */
    @Override
    @EventHandler (ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onShowCasePlayerExchangeEvent(ShowCasePlayerExchangeEvent scpee) {
        if (scpee.verify()) {
            
            Shop shop = scpee.getShop();
            
            if (!scs.canUse(scpee.getPlayer())) {
                scpee.setCancelled(true);
                scpee.setCause(new InsufficientPermissionException(Term.ERROR_INSUFFICIENT_PERMISSION.get()));
                return;
            }
            
            int amountHas     = ItemStackUtilities.countCompatibleItemStacks(
                    scpee.getPlayer().getInventory(),
                    scpee.getShop().getExchangeItemStack(),
                    scs.compareItemMeta(scpee.getShop().getExchangeItemStack()));
            
            // get the max amount
            int amountTo    = (int)(amountHas / scpee.getShop().getPrice());
            
            if (amountTo == 0) {
                // not enough items
                scpee.setCancelled(true);
                scpee.setCause(new InsufficientResourcesException(Term.ERROR_INSUFFICIENT_ITEMS_EXCHANGE.get()));
                return;
            }
            
            if (amountTo > scpee.getShop().getAmount()) {
                amountTo = scpee.getShop().getAmount();
            }
            
            if (amountTo <= 0) {
                // shop is empty
                scpee.setCancelled(true);
                scpee.setCause(new InsufficientResourcesException(Term.SHOP_EMPTY_COSTUMER.get()));
                scs.sendMessageToOwner(scpee.getShop(), Term.SHOP_EMPTY_OWNER.get(
                        MaterialNames.getItemName(scpee.getShop().getItemStack())) );
                return;
            }
            
            
            // overwrite the quantity if needed
            if (amountTo < scpee.getQuantity()) {
                scpee.setQuantity(amountTo);
            }
        }
    }
    
    
}
