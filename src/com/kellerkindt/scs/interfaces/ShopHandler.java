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
package com.kellerkindt.scs.interfaces;


import java.util.Collection;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;

import com.kellerkindt.scs.shops.Shop;




public interface ShopHandler extends Iterable<Shop> {
	
	
	
        
	/**
	 * @param i Item that is currently on display
	 * @return The shop for the given Item on display or null
	 */
	public Shop	getShop (Item i);
	
	/**
	 * @param b Block of the shop
	 * @return The shop for the given block or null
	 */
	public Shop getShop (Block b);
	
	/**
	 * @param uuid	UUID of the requested shop
	 * @return The shop for the given UUID or null
	 */
	public Shop getShop (UUID uuid);
	
	/**
	 * @param i Item on display
	 * @return Whether the given Item is bound to a shop
	 */
	public boolean isShopItem	(Item  i);
	
	/**
	 * @param b Block to check
	 * @return Whether the given Block is a shop block
	 */
	public boolean isShopBlock	(Block b);
	
	/**
	 * Adds the given Shop
	 * Will also update the UUID if
	 * it is already given to another shop
	 * @param p Shop to add
	 */
	public void	addShop		(Shop p);
	
	/**
	 * Adds the given shop
	 * and overwrites an existing with the
	 * same UUID if it exists
	 * @param shop		Shop to add
	 * @param overwrite	Whether to overwrite existing shops
	 */
	public void addShop (Shop shop, boolean overwrite);
	
	/**
	 * Adds all Shops in the given Collection
	 * Will also update the UUID if
	 * it is already given to another shop
	 * @param collection Collection to load the shops from
	 */
	public void	addAll	(Collection<Shop> collection);
	
	/**
	 * Adds all Shops in the given Collection
	 * and overwrites an existing with the
	 * same UUID if it exists
	 * @param collection 	Collection to load the shops from
	 * @param overwrite		Whether to overwrite existing shops
	 */
	public void	addAll	(Collection<Shop> collection, boolean overwrite);
	
	/**
	 * Removes the given Shop
	 * @param p Shop to remove
	 */
	public void removeShop	(Shop p);
	
	/**
	 * Removes all shop entries
	 */
	public void removeAll ();
	
	/**
	 * @param owner Name of the player to check as owner
	 * @return The amount of shops the given player owns
	 */
	public int getShopAmount(String owner);
	
	/**
	 * Loads all shops in the given chunk
	 * @param k	Chunk to load
	 */
	public void loadChunk	(Chunk k);
	
	/**
	 * Unloads all shops in the given chunk
	 * @param k Chunk to unload
	 */
	public void unloadChunk	(Chunk k);
	
	/**
	 * Hides all ShowCase-Shops
	 */
	public void hideAll	();
	
	/**
	 * Shows all ShowCase-Shops if the chunk is loaded
	 */
	public void showAll ();

	/**
	 * Shows the given Shop, if the chunk is loaded
	 * @param shop Shop to load
	 */
	public void show (Shop shop);

	/**
	 * Hides the given Shop, if it is shown
	 * @param shop Shop to hide
	 */
	public void hide (Shop shop);
	
	/**
	 * Adds an ItemFrame to the ShopHandler,
	 * which can be used instead of the floating Item,
	 * if it is above or in front of a shop
	 * (will be checked)
	 * @param frame ItemFrame to add
	 */
	public void addItemFrame (ItemFrame frame);
	
	/**
	 * This should be called if a ItemFrame was destroyed
	 * If it is above a shop, the shops' Item will be
	 * shown a again and the Item removed from the frame
	 * @param frame
	 */
	public void removeItemFrame (ItemFrame frame);
	
	/**
	 * @param frame {@link ItemFrame} to check
	 * @return Whether the given {@link ItemFrame} is known and used by ths {@link ShopHandler}
	 */
	public boolean isKnownItemFrame (ItemFrame frame);
	
	
	
	/**
	 * Starts the internal task to re-spawn the display items
	 */
	public void start ();
	
	
	/**
	 * Stops the internal task to re-spawn the display items
	 */
	public void stop();
	
	/**
	 * The size of this ShopHandler / the amount of shops in this handler
	 */
	public int size ();
	
	/**
	 * Let the {@link ShopHandler} be able to do some stuff in specific intervals
	 */
	public void tick();
	
//	events
//	/*
//	 * NOTE: not implemented at all :(
//	 * The Idea is, that the shop handler knows if the shop is interacting,
//	 * and because of that knows that the shop has changed, so it can save
//	 * the one shop.
//	 */
//	public void interact	(Block b, Player p, int amount);
//	public void info		(Block b, Player p);
}
