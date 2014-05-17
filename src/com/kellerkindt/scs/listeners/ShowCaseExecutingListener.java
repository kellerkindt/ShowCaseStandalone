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

import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseCreateEvent;
import com.kellerkindt.scs.events.ShowCaseDeleteEvent;
import com.kellerkindt.scs.events.ShowCaseEvent;
import com.kellerkindt.scs.events.ShowCaseInfoEvent;
import com.kellerkindt.scs.events.ShowCaseInteractEvent;
import com.kellerkindt.scs.events.ShowCaseItemAddEvent;
import com.kellerkindt.scs.events.ShowCaseItemRemoveEvent;
import com.kellerkindt.scs.events.ShowCaseLimitEvent;
import com.kellerkindt.scs.events.ShowCaseMemberAddEvent;
import com.kellerkindt.scs.events.ShowCaseMemberRemoveEvent;
import com.kellerkindt.scs.events.ShowCaseOwnerSetEvent;
import com.kellerkindt.scs.events.ShowCasePlayerBuyEvent;
import com.kellerkindt.scs.events.ShowCasePlayerExchangeEvent;
import com.kellerkindt.scs.events.ShowCasePlayerSellEvent;
import com.kellerkindt.scs.events.ShowCasePriceSetEvent;
import com.kellerkindt.scs.events.ShowCaseRemoveEvent;
import com.kellerkindt.scs.interfaces.ShowCaseListener;
import com.kellerkindt.scs.internals.Todo;
import com.kellerkindt.scs.shops.BuyShop;
import com.kellerkindt.scs.shops.DisplayShop;
import com.kellerkindt.scs.shops.ExchangeShop;
import com.kellerkindt.scs.shops.SellShop;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.ItemStackUtilities;
import com.kellerkindt.scs.utilities.MaterialNames;
import com.kellerkindt.scs.utilities.Term;

/**
 * This class executes the request behind the event
 * if it wasn't cancelled before
 * IMPORTANT: This listener doesn't check anything, it will
 * execute any event that is incoming! (see {@link ShowCaseVerifyingListener}
 * Also ignores a lot of issues (like full inventory)
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ShowCaseExecutingListener implements ShowCaseListener {
	
	private ShowCaseStandalone scs;
	
	public ShowCaseExecutingListener (ShowCaseStandalone scs) {
		this.scs	= scs;
	}

	/**
	 * Is called, if a player requested information about a Shop
	 * @param scie	ShowCaseInfoEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)	// monitor is to check the outcome (http://wiki.bukkit.org/Event_API_Reference)
	public void onShowCaseInfoEvent (ShowCaseInfoEvent scie) {
		Player	player	= scie.getPlayer();
		Shop	shop	= scie.getShop();
		String	owner	= scs.getPlayerName(shop.getOwner());
		
		if (shop instanceof DisplayShop) {
			scs.sendMessage(player, Term.ITEM_ON_DISPLAY.get(MaterialNames.getItemName(shop.getItemStack())));
		}
		
		if (!shop.isActive()) {
			scs.sendMessage(player, Term.INFO_1.get(shop.getClass().getSimpleName()) + ", "+Term.INFO_12.get()+", "+Term.INFO_9.get(owner));
			return;
		}
		
		// shop type + price + owner
		if(!(shop instanceof DisplayShop)) {
			scs.sendMessage(player, String.format("%-25s  %-20s  %s",
				Term.INFO_1.get(shop.getClass().getSimpleName()),
				Term.INFO_2.get(scs.formatCurrency(shop.getPrice())),
				Term.INFO_9.get(owner)));
		}
		else {
			scs.sendMessage(player, String.format("%-25s  %s",
				Term.INFO_1.get(shop.getClass().getSimpleName()),
				Term.INFO_9.get(owner)));
		}
		
		// buy shop item
		if (shop instanceof BuyShop) {
			BuyShop shopBuy	= (BuyShop)shop;
			
			scs.sendMessage(player, String.format("%-30s  %s",
					Term.INFO_4.get(MaterialNames.getItemName(shop.getItemStack())),
					Term.INFO_3.get(shop.isUnlimited() ? Term.INFO_UNLIMITED.get() : String.format("%d/%d", shopBuy.getAmount(), shopBuy.getMaxAmount()))));
		}
		
		// sell shop item
		else if (shop instanceof SellShop) {			
			scs.sendMessage(player, String.format("%-30s %s",
					Term.INFO_4.get(MaterialNames.getItemName(shop.getItemStack())),
					Term.INFO_3.get(shop.isUnlimited() ? Term.INFO_UNLIMITED.get() : String.format("%d", shop.getAmount()))));
		}
		
		// exchange shop items + enchantments
		if (shop instanceof ExchangeShop) {
			ExchangeShop shopEx	= (ExchangeShop)shop;
			
			// normal stack
			if (shopEx.getItemStack().getEnchantments().size() > 0) {
				// just show the ItemStack
				scs.sendMessage(player, String.format("&-30s %s",
						Term.INFO_4.get(MaterialNames.getItemName(shopEx.getItemStack())),
						Term.INFO_8.get("" + shopEx.getItemStack().getEnchantments().size())));
				
				
				// list enchantments
				for (Entry<Enchantment, Integer> entry : shopEx.getItemStack().getEnchantments().entrySet()) {
					scs.sendMessage(player, "  - "+entry.getKey().getName() +" "+entry.getValue());
				}
				
			} else {
				// just show the ItemStack
				scs.sendMessage(player, Term.INFO_4.get(MaterialNames.getItemName(shopEx.getItemStack())));
			}
			
			// exchange stack
			if (shopEx.getExchangeItemStack().getEnchantments().size() > 0) {
				// just show the ItemStack
				scs.sendMessage(player, String.format("&-30s %s",
						Term.INFO_4.get(MaterialNames.getItemName(shopEx.getExchangeItemStack())),
						Term.INFO_8.get("" + shopEx.getExchangeItemStack().getEnchantments().size())));
				
				
				// list enchantments
				for (Entry<Enchantment, Integer> entry : shopEx.getExchangeItemStack().getEnchantments().entrySet()) {
					scs.sendMessage(player, "  - "+entry.getKey().getName() +" "+entry.getValue());
				}
				
			} else {
				// just show the ItemStack
				scs.sendMessage(player, Term.INFO_4.get(MaterialNames.getItemName(shopEx.getExchangeItemStack())));
			}
			
			
		// others enchantments
		} else if (shop.getItemStack().getEnchantments().size() > 0) {
			// enchantments here
			scs.sendMessage(player, Term.INFO_8.get("" + shop.getItemStack().getEnchantments().size()));
			
			// list enchantments
			for (Entry<Enchantment, Integer> entry : shop.getItemStack().getEnchantments().entrySet()) {
				scs.sendMessage(player, "  - "+entry.getKey().getName() +" "+entry.getValue());
			}
		}
	}
	
	/**
	 * Is called, if a player requested an interaction between him and the shop
	 * @param scie	ShowCaseInteractEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseInteractEvent (ShowCaseInteractEvent scie) {
		
		Player	player	= scie.getPlayer();
		Shop	shop	= scie.getShop();
		Todo	todo	= scie.getTodo();
		double	amount	= todo != null ? todo.Amount : 0;
		
		ShowCaseEvent	event	= null;
		
		// nothing to do here
		if (todo == null) {
			// just to be really really sure ^^
			if (shop == null) {
				return;
			}
			
			// quantity is one by default, if the player is sneaking it's its set sneak amount
			int quantity = player.isSneaking() ? scs.getPlayerSessionHandler().getSession(player).getUnitSize() : 1;
			
			// buy / sell / exchange
			if (shop instanceof BuyShop) {
				event 	= new ShowCasePlayerSellEvent(player, (BuyShop)shop, quantity);
				
			} else if (shop instanceof SellShop) {
				event 	= new ShowCasePlayerBuyEvent(player, (SellShop)shop, quantity);
				
			} else if (shop instanceof ExchangeShop) {
				event 	= new ShowCasePlayerExchangeEvent(player, (ExchangeShop)shop, quantity);
				
			} else if (shop instanceof DisplayShop) {
				event = new ShowCaseInfoEvent(player, shop);
				
			}
			
			
			
		} else {
			switch (todo.Type) {
				// add items
				case ADD_ITEMS:
					event 	= new ShowCaseItemAddEvent(player, shop, (int)amount, shop.getItemStack());
					break;
					
				// add a member
				case ADD_MEMBER:
					event 	= new ShowCaseMemberAddEvent(player, shop, todo.String);
					event.setMsgSuccessfully(Term.MESSAGE_SUCCESSFULL_ADDED_MEMBER.get());
					break;
					
				// create a new shop
				case CREATE:
					event 	= new ShowCaseCreateEvent(player, shop);
					event.setMsgSuccessfully(Term.MESSAGE_SUCCESSFULL_CREATED.get());
					break;
					
				// deletes a shop
				case DESTROY:
					event	= new ShowCaseDeleteEvent(player, shop);
					event.setMsgSuccessfully(Term.MESSAGE_SUCCESSFULL_DESTROYED.get());
					break;
					
				// remove items
				case GET_ITEMS:
					event 	= new ShowCaseItemRemoveEvent(player, shop, (int)amount, shop.getItemStack());
					break;
					
				// set the new limit of a shop
				case LIMIT:
					event 	= new ShowCaseLimitEvent(player, shop, (int)amount);
					break;
					
				// remove a shop
				case REMOVE:
					event 	= new ShowCaseRemoveEvent(player, shop);
					event.setMsgSuccessfully(Term.MESSAGE_SUCCESSFULL_REMOVED.get());
					break;
					
				// removes a member
				case REMOVE_MEMBER:
					event	= new ShowCaseMemberRemoveEvent(player, shop, todo.String);
					event.setMsgSuccessfully(Term.MESSAGE_SUCCESSFULL_REMOVED_MEMBER.get());
					break;
					
				// sets the new owner of a shop
				case SET_OWNER:
					
					event	= new ShowCaseOwnerSetEvent(player, shop, todo.String);
					event.setMsgSuccessfully(Term.MESSAGE_SET_OWNER.get(todo.String));
					break;
					
				// sets the new price of a shop
				case SET_PRICE:
					event 	= new ShowCasePriceSetEvent(player, shop, amount);
					break;
					
				default:
					scs.log(Level.SEVERE, "UNKNOWN TODO.TYPE - PLEASE CONTACT A DEVELOPER", false);
					break;
				
			}
			
		}
		if (event != null) {
			// perform the event
			scs.callShowCaseEvent(event);
			
			// check for an error
			if (event.isCancelled() && event.getCause() != null) {
				// an error occurred
				scs.sendMessage(scie.getPlayer(), event.getCause().getMessage());
				
				
			} else if (!event.isCancelled() && event.getMsgSuccessfully() != null) {
				/// successfully
				scs.sendMessage(scie.getPlayer(), event.getMsgSuccessfully());
			}
			
			
		}
	}
	
	/**
	 * Is called, if items should be added to a shop
	 * @param sciae	ShowCaseItemAddEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseItemAddEvent (ShowCaseItemAddEvent sciae) {
		System.out.println ("going to add item");
		Shop			shop	= sciae.getShop();
		ExchangeShop	shopEx	= shop instanceof ExchangeShop ? ((ExchangeShop)shop) : null;
		int				amount	= sciae.getAmount();
		
		if (shop.getItemStack().isSimilar(sciae.getItemStack())) {
			// add the amount
			shop.setAmount(shop.getAmount() + amount);
			
			// set the message
			sciae.setMsgSuccessfully(Term.INVENTORY_UPDATE.get(""+amount, ""+shop.getAmount()));
		
		} else if (shopEx != null && shopEx.getExchangeItemStack().isSimilar(sciae.getItemStack())) {
		
			// add the amount
			shopEx.setExchangeAmount(shopEx.getExchangeAmount() + amount);
			
			// set the message
			sciae.setMsgSuccessfully(Term.INVENTORY_UPDATE.get(""+amount, ""+shopEx.getExchangeAmount()));
		}
	}
	
	/**
	 * Is called, if a member should be added to a shop
	 * @param scmae	ShowCaseMemberAddEvent with needed information about the shop and the member to add
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseMemberAddEvent (ShowCaseMemberAddEvent scmae) {
		Shop	shop	= scmae.getShop();
		String	member	= scmae.getMember();
		
		shop.addMember( scs.getPlayerUUID(member) );
	}
	
	/**
	 * Is called, if a new shop should be created
	 * @param scce	ShowCaseCreateEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseCreateEvent (ShowCaseCreateEvent scce) {
		// get the shop to add
		Shop	shop	= scce.getShop();
		double	cost	= scs.getCreatePrice(shop.getClass());
		
		// add the shop
		scs.getShopHandler().addShop(shop);
		scs.getShopHandler().show	(shop);
		
		// remove the money
		scs.getBalanceHandler().sub(scce.getPlayer(), cost);
	}
	
	/**
	 * Is called, if a shop should be deleted
	 * @param scde	ShowCaseDeleteEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseDeleteEvent (ShowCaseDeleteEvent scde) {
		scs.getShopHandler().hide		(scde.getShop());
		scs.getShopHandler().removeShop	(scde.getShop());
	}	
	
	/**
	 * Is called, if items should be removed from a shop
	 * @param scire	ShowCaseItemRemoveEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseItemRemoveEvent (ShowCaseItemRemoveEvent scire) {
		
		Shop			shop	= scire.getShop();
		ExchangeShop	shopEx	= shop instanceof ExchangeShop ? ((ExchangeShop)shop) : null;
		int				amount	= scire.getAmount();
		
		if (shop.getItemStack().isSimilar(scire.getItemStack())) {
			
			// add the amount
			shop.setAmount(shop.getAmount() - amount);
			
			// set the message
			scire.setMsgSuccessfully(Term.INVENTORY_UPDATE.get(""+amount, ""+shop.getAmount()));
		
		} else if (shopEx != null && shopEx.getExchangeItemStack().isSimilar(scire.getItemStack())) {
		
			// add the amount
			shopEx.setExchangeAmount(shopEx.getExchangeAmount() - amount);
			
			// set the message
			scire.setMsgSuccessfully(Term.INVENTORY_UPDATE.get(""+amount, ""+shopEx.getExchangeAmount()));
		}
	}
	
	
	/**
	 * Is called, if a new limit should set for the shop
	 * @param scle ShowCaseLimitEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseLimitEvent (ShowCaseLimitEvent scle) {
		if (scle.getShop() instanceof BuyShop) {
			BuyShop shop = (BuyShop)scle.getShop();
			
			shop.setMaxAmount(scle.getLimit());
			scle.setMsgSuccessfully(Term.MESSAGE_BUY_LIMIT.get(""+shop.getMaxAmount()));
		}
	}
	
	/**
	 * Is called, if the shop should be removed
	 * @param scre	ShowCaseRemoveEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseRemoveEvent (ShowCaseRemoveEvent scre) {
		scs.getShopHandler().hide		(scre.getShop());
		scs.getShopHandler().removeShop	(scre.getShop());
	}
	
	/**
	 * Is called, if a member should be removed
	 * @param scmre	ShowCaseMemeberRemoveEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseMemberRemoveEvent (ShowCaseMemberRemoveEvent scmre) {
		scmre.getShop().removeMember( scs.getPlayerUUID(scmre.getMember()) );
	}
	
	/**
	 * Is called, if the owner of a shop should be set to another one
	 * @param scose	ShowCaseOwnerSetEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCaseOwnerSetEvent (ShowCaseOwnerSetEvent scose) {
		scose.getShop().setOwner( scs.getPlayerUUID(scose.getNewOwnerName()) );
		
	}
	
	/**
	 * Is called, if the price of a shop should be set to another one
	 * @param scpse ShowCasePriceSetEvent with needed information about the shop and player
	 */
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCasePriceSetEvent (ShowCasePriceSetEvent scpse) {
		scpse.getShop().setPrice(scpse.getPrice());
		scpse.setMsgSuccessfully(Term.MESSAGE_SET_PRICE.get(""+scpse.getPrice()));
	}


	/**
	 * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCasePlayerBuyEvent(com.kellerkindt.scs.events.ShowCasePlayerBuyEvent)
	 */
	@Override
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCasePlayerBuyEvent(ShowCasePlayerBuyEvent scpbe) {
		
		Shop	shop	= scpbe.getShop();
		
		int 	added = ItemStackUtilities.addToInventory(scpbe.getPlayer().getInventory(), shop.getItemStack(), scpbe.getQuantity());
		double	price = added * shop.getPrice();
		
		if (!shop.isUnlimited()) {
			// the owner only gets the money if the shop is not unlimited
			scs.getBalanceHandler().add(shop.getOwner(),   price);
		}
		
		// the costumer always has to pay the items
		scs.getBalanceHandler().sub(scpbe.getPlayer(), price);
		
		// ignore unlimited - later you can see how many were sold ^^
		shop.setAmount(shop.getAmount() - added);
		
		// contact the owner
		scs.sendTransactionMessageToOwner(shop, Term.MESSAGE_SELL_OWNER_1.get(shop.getItemStack().getType().toString(), ""+shop.getAmount()));
		scs.sendTransactionMessageToOwner(shop, Term.MESSAGE_SELL_OWNER_2.get(scpbe.getPlayer().getDisplayName(), ""+scpbe.getQuantity(), ""+price));

		// set successfully message
		scpbe.setMsgSuccessfully(Term.MESSAGE_SELL_COSTUMER.get(MaterialNames.getItemName(shop.getItemStack()), ""+added, ""+price));
	}


	/**
	 * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCasePlayerSellEvent(com.kellerkindt.scs.events.ShowCasePlayerSellEvent)
	 */
	@Override
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCasePlayerSellEvent(ShowCasePlayerSellEvent scpse) {
		
		Player		player	= scpse.getPlayer();
		BuyShop 	shop 	= scpse.getShop();
		
		int 	removed = ItemStackUtilities.removeFromInventory(player.getInventory(), shop.getItemStack(), scpse.getQuantity(), scs.compareItemMeta(shop.getItemStack()));
		double	price	= removed * shop.getPrice();
		
		
		if (!shop.isUnlimited()) {
			// the owner only looses its money if it isn't a unlimited shop
			scs.getBalanceHandler().sub(shop.getOwner(),	price);
		}
		
		// the costumer has always to get its money
		scs.getBalanceHandler().add(player, 			price);
		
		
		// add the amount that was removed
		shop.setAmount(shop.getAmount() + removed);
		
		// contact the owner
		scs.sendTransactionMessageToOwner(shop, Term.MESSAGE_BUY_OWNER_1.get(shop.getItemStack().getType().toString(), ""+shop.getAmount(), ""+shop.getMaxAmount()));
		scs.sendTransactionMessageToOwner(shop, Term.MESSAGE_BUY_OWNER_2.get(scpse.getPlayer().getDisplayName(), ""+scpse.getQuantity(), ""+price));
		
		// set successfully message
		scpse.setMsgSuccessfully(Term.MESSAGE_BUY.get(MaterialNames.getItemName(shop.getItemStack()), ""+removed, ""+price));
	}


	/**
	 * @see com.kellerkindt.scs.interfaces.ShowCaseListener#onShowCasePlayerExchangeEvent(com.kellerkindt.scs.events.ShowCasePlayerExchangeEvent)
	 */
	@Override
	@EventHandler (ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onShowCasePlayerExchangeEvent(ShowCasePlayerExchangeEvent scpee) {
		
		// 2013-0X-XX dude, check it ^^
		// 2014-03-02 found and fixed issue xD
		
		Player			player	= scpee.getPlayer();
		ExchangeShop	shop 	= scpee.getShop();
		double			price	= shop.getPrice();
		
		int 	buyAmount		= scpee.getQuantity();
		double 	removeAmount	= buyAmount * price;
		
		// it always gets more expensive
		if (removeAmount > ((int)removeAmount)) {
			removeAmount = ((int)removeAmount) +1;
		}
		
		// do not divide by 0 or less
		if (removeAmount <= 0) {
			return;
		}
		
		int removed	= ItemStackUtilities.removeFromInventory(
				player.getInventory(),
				shop.getExchangeItemStack(),
				(int)removeAmount,
				scs.compareItemMeta(shop.getExchangeItemStack()));
		int toAdd	= (int)(removed / price);
		
		// add items for that was payed
		ItemStackUtilities.addToInventory(player.getInventory(), shop.getItemStack(), toAdd);
		
		// remove the items from the shop
		shop.setAmount			( shop.getAmount() 			- toAdd 	);
		shop.setExchangeAmount	( shop.getExchangeAmount() 	+ removed 	);
		

		// contact the owner
		scs.sendTransactionMessageToOwner(shop, Term.MESSAGE_SELL_OWNER_1.get(shop.getItemStack().getType().toString(), ""+shop.getAmount()));
		scs.sendTransactionMessageToOwner(shop, Term.MESSAGE_SELL_OWNER_2.get(scpee.getPlayer().getDisplayName(), ""+scpee.getQuantity(), ""+price));
		
		scpee.setMsgSuccessfully(Term.MESSAGE_SELL_COSTUMER.get(MaterialNames.getItemName(shop.getItemStack()), ""+removed, ""+toAdd));
	}
}
