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
package com.kellerkindt.scs.internals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseOwnerSetEvent;
import com.kellerkindt.scs.events.ShowCaseShopHandlerChangedEvent;
import com.kellerkindt.scs.interfaces.ShopHandler;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.ItemStackUtilities;


public class SimpleShopHandler implements ShopHandler {

	// //I've converted this to a fixed array, because arrays are a lot quicker,
	// //and lower mem impact. Since we have a sync repeating thread that loops
	// through
	// //the array, i've made it fixed. I use temp array lists to add/remove
	// shops.
	// private Shop[] shops = new Shop[0];
	// --> HashMap is a lot faster for our needs

	private HashMap<Item, Shop> 			itemShops 	= new HashMap<Item, Shop>();
	private HashMap<Shop, Item>				shopItems	= new HashMap<Shop, Item>();
	private HashMap<Block, Shop> 			blockShops 	= new HashMap<Block, Shop>();
	private HashMap<UUID, Shop>				uuidShops	= new HashMap<UUID, Shop>();
	private HashMap<UUID, Integer>			shopOwners	= new HashMap<UUID, Integer>();
	private HashMap<Shop, List<ItemFrame>>	shopFrames	= new HashMap<Shop, List<ItemFrame>>();
	private HashMap<ItemFrame, Shop>		frameShops	= new HashMap<ItemFrame, Shop>();
	private ArrayList<Shop>					shops		= new ArrayList<Shop>();	// for fast iteration
	
	private ShowCaseStandalone 			scs 		= null;
	private boolean 					fireEvents = true;

	private InternalShopChangeListener changeListener;
	
	public SimpleShopHandler(ShowCaseStandalone scs) {
		this.scs 			= scs;
		this.changeListener = new InternalShopChangeListener();
		
		scs.getServer().getPluginManager().registerEvents( this.changeListener, scs );
	}
	
	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#tick()
	 */
	@Override
	public void tick() {
		checkShopDisplayState();
	}

	/**
	 * This method displays or hides the shops
	 * if the item disappeared, or the shop got
	 * inactive
	 * NOTE: this must be called from a synchronized Bukkit Thread 
	 */
	public void checkShopDisplayState() {
		long start = System.nanoTime();
		
		// show some debug information
		if (scs.getConfiguration().isDebuggingThreads()) {
			ShowCaseStandalone.dlog("Refreshing items. Thread exec start: " + start);
		}

		for (Shop p : this) {
			try {
				// Regular chunk.isloaded causes bukkit to load the
				// chunk.
				if (!isChunkLoaded(p.getSpawnLocation()))
					continue;
				
				// shop Item
				Item item = shopItems.get(p);
	
				if (scs.getConfiguration().isHidingInactiveShops() && (!p.isActive() && p.isVisible()))
					hide(p);
	//				itemShops.remove(p.hide());
	
				else if (item == null) {
					if (!scs.getConfiguration().isHidingInactiveShops() || p.isActive())
						show(p);
	//					itemShops.put(p.show(), p);
					else
						hide(p);
	//					itemShops.remove(p.hide());
	
				} else if (item.isDead()) {
					if (!scs.getConfiguration().isHidingInactiveShops() || p.isActive()) {
	
						hide(p);
						show(p);
	//					itemShops.remove(p.hide());
	//					itemShops.put(p.show(), p);
						
					} else {
						hide(p);
	//					itemShops.remove(p.hide());
					}
				}
			} catch (Throwable t) {
				scs.log(Level.SEVERE, "Error while handling shop="+p, false);
				t.printStackTrace();
			}
		}

		if (scs.getConfiguration().isDebuggingThreads()) {
			long end = System.nanoTime();
			long net = end - start;
			ShowCaseStandalone.dlog("Thread exec end: " + end);
			ShowCaseStandalone.dlog("Net time: " + net);
		}
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#getShop(org.bukkit.entity.Item)
	 */
	@Override
	public Shop getShop(Item i) {
		return itemShops.get(i);
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#getShop(org.bukkit.block.Block)
	 */
	@Override
	public Shop getShop(Block b) {
		return blockShops.get(b);
	}
	
	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#getShop(java.util.UUID)
	 */
	@Override
	public Shop getShop(UUID uuid) {
		return uuidShops.get(uuid);
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#isShopItem(org.bukkit.entity.Item)
	 */
	@Override
	public boolean isShopItem(Item item) {
		return itemShops.containsKey(item);
	}

	@Override
	public boolean isShopBlock(Block b) {
		return blockShops.containsKey(b);
	}

	private void addRaw(Shop p, boolean overwrite) {
		// if not already set a UUID, set it now
		if (p.getUUID() == null) {
			p.setUUID(UUID.randomUUID());
			scs.log(Level.INFO, "Adding shop without UUID, UUID set to "+p.getUUID(), false);
		}
		
		// be sure that the UUID is unique
		while (uuidShops.containsKey(p.getUUID()) && !overwrite) {
			p.setUUID(UUID.randomUUID());
		}
		
		if (uuidShops.containsKey(p.getUUID()) && overwrite) {
			removeShop(uuidShops.get(p.getUUID()));
		}
		
		// add to lists
		blockShops	.put(p.getBlock(), 	p);
		uuidShops	.put(p.getUUID(), 	p);
		shops		.add(p);
		
		this.setFrames(p);
		this.incrementShopAmount( p.getOwner() );
	}
	
	/**
	 * Links all found ItemFrames
	 * @param shop Shop to search ItemFrames for
	 */
	private void setFrames (Shop shop) {
		
		// clear
		shopFrames.put(shop, new ArrayList<ItemFrame>());
		
		// Iterate through the found frames
		for (ItemFrame frame : getItemFrames(shop)) {
			addFrame(shop, frame);
		}
	}
	
	/**
	 * Removes the link between the given Shop
	 * and all known ItemFrames
	 * @param shop Shop to find ItemFrames for
	 */
	private void removeFrames (Shop shop) {
		// get list
		List<ItemFrame> frames = shopFrames.get(shop);
		
		// is valid?
		if (frames != null) {
			
			// iterate through the links
			for (ItemFrame frame : frames) {
				
				// remove frame to shop link
				frameShops.remove(frame);
			}
			
			// clear
			frames.clear();
		}
	}
	
	/**
	 * Adds a ItemFrame to the List for a shop
	 * @param shop 	Shop to attach to
	 * @param frame	Frame to attach
	 */
	private void addFrame (Shop shop, ItemFrame frame) {
		// get list
		List<ItemFrame> list = shopFrames.get(shop);
		
		// list does not exist yet?
		if (list == null) {
			list = new ArrayList<ItemFrame>();
			shopFrames.put(shop, list);
		}
		
		// add frame
		list.add(frame);
		
		// add frame to shop link
		frameShops.put(frame, shop);
	}
	
	/**
	 * Removes all links for the given ItemFrame
	 * @param frame	ItemFrame to remove
	 */
	private void removeFrame (ItemFrame frame) {
		
		// remove frame to shop link
		Shop shop = frameShops.remove(frame);
		
		// remove shop to frame link
		shopFrames.get(shop).remove(frame);
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#addShop(com.kellerkindt.scs.shops.Shop)
	 */
	@Override
	public void addShop(Shop p) {
		addShop(p, false);
	}
	
	@Override
	public void addShop(Shop p, boolean overwrite) {
		addRaw(p, overwrite);

		fireChangeEvent();
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#removeShop(com.miykeal.showCaseStandalone.shops.Shop)
	 */
	@Override
	public void removeShop(Shop shop) {
		// remove the main part
		removeShopIteratorFriendly(shop);
		
		// remove from iterator list
		shops.remove(shop);
	}
	
	/**
	 * Removes the Shop from every Map but the List for the iterator
	 * This should allow the Iterator to remove shops while iterating
	 * @param shop Shop to remove
	 */
	public void removeShopIteratorFriendly (Shop shop) {
		// hide
		hide(shop);
	
		blockShops	.remove(shop.getBlock());
		uuidShops	.remove(shop.getUUID());
//		shops		.remove(shop);
		itemShops	.remove( shopItems	.remove(shop) );
	
		this.removeFrames(shop);
		this.decrementShopAmount( shop.getOwner() );
		
		fireChangeEvent();
	}
	
	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#getShopAmount(java.lang.String)
	 */
	@Override
	public int getShopAmount( String owner ) {
		Integer amount = this.shopOwners.get( owner );
		
		return ( amount != null ? amount : 0 );
	}
	
	
	private void incrementShopAmount( UUID shopOwner ) {
		Integer amount = this.shopOwners.get( shopOwner );
		
		if ( amount == null ) {
			this.shopOwners.put( shopOwner, 1 );
		} else {
			this.shopOwners.put( shopOwner, amount + 1 );
		}
	}
	
	private void decrementShopAmount( UUID shopOwner ) {
		Integer amount = this.shopOwners.get( shopOwner );
		
		if ( amount == null ) {
			this.shopOwners.put( shopOwner, 1 );
		} else if ( amount > 0 ){
			this.shopOwners.put( shopOwner, amount - 1 );
		}
	}
	

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#loadChunk(org.bukkit.Chunk)
	 */
	@Override
	public void loadChunk(Chunk k) {
		if (scs.getConfiguration().isDebuggingChunks()) {
			ShowCaseStandalone.dlog("Load chunk: " + k.toString() + ", " + k.getWorld().getName());
		}

		// fix for #263?
		if (k == null || k.getWorld() == null) {
			return;
		}

		if (scs.getConfiguration().isDebuggingChunks()) {
			ShowCaseStandalone.dlog("Load chunk: " + k.toString() + ", " + k.getWorld().getName());
		}

		try {
			for (Shop p : this) {

				if (p.getWorld() == null) {
					ShowCaseStandalone.slog(Level.SEVERE, "Found showcase on not existing world! To remove perform: /scs purge u:" + p.getWorldUUID());
					continue;
				}

				double 	kx = k.getX();
				double 	kz = k.getZ();
				World 	kw = k.getWorld();

				Chunk 	ck = p.getLocation().getChunk();
				double 	px = ck.getX();
				double 	pz = ck.getZ();
				World 	pw = ck.getWorld();

				if (kx == px && kz == pz && kw.getName().equals(pw.getName())) {
					if (scs.getConfiguration().isDebuggingChunks()) {
						ShowCaseStandalone.dlog("Found scs to load: " + p.getUUID());
					}

					if (!scs.getConfiguration().isHidingInactiveShops() || p.isActive())
						show(p);
//						itemShops.put(p.show(), p);
					else
						hide(p);
//						itemShops.remove(p.hide());
				}
			}
		} catch (NullPointerException npe) {
			ShowCaseStandalone.slog(Level.WARNING,
					"NPE on load chunk shop enable.");
			npe.printStackTrace();
		}
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#unloadChunk(org.bukkit.Chunk)
	 */
	@Override
	public void unloadChunk(Chunk k) {
		if (scs.getConfiguration().isDebuggingChunks()) {
			ShowCaseStandalone.dlog("Unload chunk: " + k.toString() + ", "
					+ k.getWorld().getName());
		}

		try {
			for (Shop p : this) {

				if (p.getWorld() == null) {
					ShowCaseStandalone.slog(Level.SEVERE, "Found showcase in not existing world! To remove perform: /scs purge u:" + p.getWorldUUID());
					continue;
				}

				double	kx = k.getX();
				double 	kz = k.getZ();
				World 	kw = k.getWorld();

				Chunk 	ck = p.getLocation().getChunk();
				double 	px = ck.getX();
				double 	pz = ck.getZ();
				World	pw = ck.getWorld();

				if (kx == px && kz == pz && kw.getName().equals(pw.getName())) {

					if (scs.getConfiguration().isDebuggingChunks()) {
						ShowCaseStandalone.dlog("Found scs to unload: " + p.getUUID());
					}
					
					// hide
					hide(p);
				}
			}

		} catch (NullPointerException npe) {
			ShowCaseStandalone.slog(Level.WARNING, "NPE on unload chunk shop disable.");
		}
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#hideAll()
	 */
	@Override
	public void hideAll() {
		for (Shop p : this)
			hide(p);
//			itemShops.remove(p.hide());
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#showAll()
	 */
	@Override
	public void showAll() {
		for (Shop p : this) {
			if (p.getBlock() != null) {
				if (p.getBlock().getChunk().isLoaded()) {
					if (!scs.getConfiguration().isHidingInactiveShops() || p.isActive()) {
						show(p);
					}
				}
			}
		}
	}


	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#stop()
	 */
	@Override
	public void stop() {
//		scs.getServer().getScheduler().cancelTask(syncTask);
//		ShowCaseStandalone.slog(Level.FINEST, "Stopped sync task.");
	}

	@Override
	public void start() {
//		checkShopDisplayState();
//		ShowCaseStandalone.slog(Level.FINEST, "Started sync task.");
	}

//	/**
//	 * @see com.kellerkindt.scs.interfaces.ShopHandler#interact(org.bukkit.block.Block,
//	 *      org.bukkit.entity.Player, int)
//	 */
//	@Override
//	public void interact(Block b, Player p, int amount) {
//		BenchMark bm = null;
//		if (Properties.interactDebug)
//			bm = new BenchMark("Handler interact");
//
//		Shop sp = this.getShopForBlock(b);
//		if (Properties.interactDebug)
//			bm.mark("getShopForBlock");
//
//		sp.interact(p, amount);
//		if (Properties.interactDebug)
//			bm.mark("after interact");
//
//		if (Properties.interactDebug)
//			bm.end();
//	}

//	/**
//	 * @see com.kellerkindt.scs.interfaces.ShopHandler#info(org.bukkit.block.Block,
//	 *      org.bukkit.entity.Player)
//	 */
//	@Override
//	public void info(Block b, Player p) {
//		Shop sp = this.getShopForBlock(b);
//		sp.info(p);
//	}

	private boolean isChunkLoaded(Location loc) {
		int cx = loc.getBlockX() >> 4;
		int cy = loc.getBlockZ() >> 4;
		World cw = loc.getWorld();

		// fix for #263 ?
		if (cw == null)
			return false;

		return cw.isChunkLoaded(cx, cy);
	}

	@Override
	public void removeAll() {
		blockShops.clear();
		uuidShops.clear();
		itemShops.clear();
		fireChangeEvent();
	}

	@Override
	public Iterator<Shop> iterator() {
		// DAFUQ, managing and keeping a ArrayList up to date, but iterating through the slow Map-Iterator - WHY!?
//		return blockShops.values().iterator();
		
		// iterate through the fast ArrayList-Iterator
		return new ShopIterator(shops.iterator());
	}
	
	@Override
	public void addAll(Collection<Shop> collection) {
		addAll(collection, false);
	}

	@Override
	public void addAll(Collection<Shop> shops, boolean overwrite) {
		// deactivate event firing
		fireEvents = false;

		// add shops
		for (Shop p : shops) {
			
			// is shop valid?
			if (p == null) {
				ShowCaseStandalone.slog(Level.WARNING, "Ignoring broken shop");
				continue;
			}
			
			addShop(p, overwrite);
		}

		// enable event firing
		fireEvents = true;

		// fire change event
		fireChangeEvent();
	}

	/**
	 * Fires the ShopHandlerChangedValues event
	 */
	private void fireChangeEvent() {
		// active?
		if (!fireEvents)
			return;

		// perform the Event
		scs.getServer().getPluginManager().callEvent(new ShowCaseShopHandlerChangedEvent(this));
	}

	
	private class InternalShopChangeListener implements Listener {
		
		@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
		public void onShowCaseChange(ShowCaseOwnerSetEvent event) {
			if (!event.getNewOwnerName().equals( scs.getPlayerName( event.getShop().getOwner() ))) {

				// the old owner has now one shop less
				decrementShopAmount( event.getShop().getOwner() );
				
				// the new owner has now one shop more
				incrementShopAmount( scs.getPlayerUUID(event.getNewOwnerName()) );
			}
		}
	}



	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#hide(com.kellerkindt.scs.shops.Shop)
	 */
	@Override
	public void hide(Shop shop) {
		// get the Item for this shop
		Item item	= shopItems.get(shop);
		
		if (item != null) {
	        
	        // prepare teleportation to the ground
	        int		x	= shop.getSpawnLocation().getBlockX();
	        int 	y 	= 0;
	        int 	z	= shop.getSpawnLocation().getBlockZ();
	        World	w	= shop.getSpawnLocation().getWorld();
	        
	        // teleport (fixed some client sided issues)
	        item.teleport(new Location(w, x, y, z));

	        // remove item
	        item.remove();
	        
	        // set invisible
	        shop.setVisible(false);
	        
	        // remove item
	        shopItems.remove(shop);
	        itemShops.remove(item);
	        
	        
		}
		
		// Item Frame?
		List<ItemFrame>	frames	= shopFrames.get(shop);
		
		if (frames != null) {
			// go through all frames
			for (ItemFrame frame : frames) {
				// set to no item
				frame.setItem(null);
			}
		}
		
	}
	
	
	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#show(com.kellerkindt.scs.shops.Shop)
	 */
	@Override
	public void show(Shop shop) {
		// get ItemFrame
		List<ItemFrame> frames = shopFrames.get(shop);
		
		
		// is ItemFrame?
		if (frames != null && frames.size() > 0) {
			// hide - just to be sure
			hide(shop);
			
			// TODO: bad performance?
			// set Item in the frame
			for (ItemFrame frame : frames) {
				frame.setItem(shop.getItemStack().clone());
			}
			
			// set visible
			shop.setVisible(true);
		}
		
		else {
			// spawn location set? world valid?
			if (shop.getSpawnLocation() == null || shop.getWorld() == null) {
				// no spawn location set
				return;
			}
			
			// check for duplicate item
			checkDuplicateItem(shop);
	
			// check whether the item is valid
			if (shop.getItemStack() == null) {
				scs.log(Level.WARNING, "Cannot display damaged shop, UUID="+shop.getUUID(), false);
				return;
			}
			
			// spawn new item
			Location 	spawnLocation	= shop.getSpawnLocation();
			ItemStack	itemStack		= shop.getItemStack().clone();
			
			if (Properties.DEFAULT_STACK_TO_MAX) {
				itemStack.setAmount(itemStack.getMaxStackSize());
			} else {
				/*
				 *  barrier 1, an amount of 0 does not cause any pickup event (seems to be so)
				 *  !! Mobs can pick the Item up, but do not drop it, since it has an amount of 0,
				 *     although they can use it ^^
				 */
				itemStack.setAmount(Properties.DEFAULT_STACK_AMOUNT);
			}
			
			
			
			Item item = shop.getWorld().dropItem(spawnLocation, itemStack);
			
			// prevent item from being merged (at least in some cases)
			item.getItemStack().getItemMeta().setDisplayName(UUID.randomUUID().toString());
			
			
			/*
			 *  barrier 2, astronomy high pickup delay which can't
			 *  be reached in a normal Item life
			 *  Does not work on mobs (mc1.7.9)
			 */
			item.setPickupDelay(Properties.DEFAULT_PICKUP_DELAY);
			item.setVelocity(new Vector(0, 0.01, 0));
			
			// add to lists
			shopItems.put(shop, item);
			itemShops.put(item, shop);
			
			// set visible
			shop.setVisible(true);
		}
	}
	
	/**
	 * @param shop Shop to check
	 * @return The ItemFrame above or null if there is not ItemFrame set
	 */
	public List<ItemFrame> getItemFrames (Shop shop) {
		
		// list to return
		List<ItemFrame> list = new ArrayList<ItemFrame>();
		
		// block valid? world valid?
		if (shop.getBlock() != null && shop.getWorld() != null)  {
//			
			
			// get Blocks
			Block blockUp 	= shop.getBlock().getRelative(BlockFace.UP);
			Block blockDown	= shop.getBlock();

			Location locUp	= blockUp	.getLocation();
			Location locDown= blockDown	.getLocation();
			
			
			
			// Search the ItemFrame, TODO: find a faster / better solution
			for (ItemFrame frame : shop.getWorld().getEntitiesByClass(ItemFrame.class)) {

				BlockFace	face	= frame.getFacing();
				
				if (face == BlockFace.SOUTH || face == BlockFace.NORTH) {
					face = face.getOppositeFace();
				}
				
				Block fBlock	= frame.getLocation().getBlock();
				Block aBlock	= fBlock.getRelative(face);
				
				// is frame faced to the upper or lower block?
				if (aBlock.getLocation().equals(locDown) || aBlock.getLocation().equals(locUp) || fBlock.getLocation().equals(locUp)) {
					list.add(frame);
				}
			}
			
		}
		
		// not found
		return list;
	}
	
	/**
	 * Checks if there is already an item - server-crash?
	 * If found, they will be removed
	 * @return
	 */
	public void checkDuplicateItem (Shop shop) {
		
		// max difference in height
		double 		maxYDiff	= 1.5;
		Item		shopItem	= shopItems.get(shop);
		
		// check in this chunk, not in whole world --> faster
		for (Entity e : shop.getLocation().getChunk().getEntities())
		{
			
			double x = e.getLocation().getX();
			double z = e.getLocation().getZ();
			double yDiff = shop.getSpawnLocation().getY() - e.getLocation().getY();
                       
			if (yDiff < 0)
				yDiff *= -1;

			if (x == shop.getSpawnLocation().getX() && yDiff <= maxYDiff && z == shop.getSpawnLocation().getZ()) {
				
               if (e instanceof Item) {
               	Item	item = (Item)e;
               	
               	// remove if not the current Item for this Shop
               	if (ItemStackUtilities.itemsEqual(item.getItemStack(), shop.getItemStack(), false) && shopItem != item)
               		item.remove();
               }
			}
		}
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#onItemFramePlaced(org.bukkit.entity.ItemFrame)
	 */
	@Override
	public void addItemFrame(final ItemFrame frame) {
		
		// delayed, because the ItemFrame is added after this event
		scs.getServer().getScheduler().scheduleSyncDelayedTask(scs, new Runnable() {
			
			@Override
			public void run() {
				
				// blocks to check
				List<Block>	blocks		= new ArrayList<Block>();
				
				// get main blocks
				Block frameBlock 		= frame.getLocation().getBlock();
				Block frameBlockDown	= frameBlock.getRelative(BlockFace.DOWN);
				
				// add them
				blocks.add(frameBlock);
				blocks.add(frameBlockDown);
				
				// allowed block faces
				List<BlockFace> faces 	= new ArrayList<BlockFace>();
				
				// add them
				faces.add(BlockFace.NORTH);
				faces.add(BlockFace.EAST);
				faces.add(BlockFace.SOUTH);
				faces.add(BlockFace.WEST);
				faces.add(BlockFace.DOWN);
				
				// iterate through the blocks
				for (Block block : blocks) {
				
					// iterate through all block faces
					for (BlockFace face : faces) {
						
						Block 	blockRelative 	= block.getRelative(face);
						Shop	shop			= getShop(blockRelative);
						
						// Shop found?
						if (shop != null) {
							
							// add the frames
							setFrames(shop);

							// apply changes
							hide(shop);
							show(shop);
						
						}
					}
				}
				
			}
		});
	}
	
	@Override
	public boolean isKnownItemFrame(ItemFrame frame) {
		return frameShops.containsKey(frame);
	}

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#onItemFrameDestroyed(org.bukkit.entity.ItemFrame)
	 */
	@Override
	public void removeItemFrame(ItemFrame frame) {
		
		final Shop shop	= frameShops.get(frame);
		
		// not above a shop?
		if (shop == null) {
			return;
		}
		
		// above a shop, hide item and show in ItemFrame
		else {
			// remove item
			frame.setItem(null);
			
			// remove frame
			removeFrame(frame);
			
//			// remove frame
//			shopFrames.remove(shop);
			
			// delayed, because the ItemFrame is removed after this event
			scs.getServer().getScheduler().scheduleSyncDelayedTask(scs, new Runnable() {
				
				@Override
				public void run() {

					hide(shop);
					show(shop);
				}
			});
		}
	}
	
	
	

	/**
	 * @see com.kellerkindt.scs.interfaces.ShopHandler#size()
	 */
	@Override
	public int size() {
		return shops.size();
	}
	
	
	/**
	* This Iterator makes it possible to remove items with ".remove()"
	* Got the suggestion in Ticket #465 (Phoenix_IV)
	* @author kellerkindt <michael at kellerkindt.com>
	*/
	private class ShopIterator implements Iterator<Shop> {
		
		private Iterator<Shop>		iterator;
		private Shop				shop	= null;
		
		public ShopIterator (Iterator<Shop> iterator) {
			this.iterator	= iterator;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Shop next() {
			return (shop = iterator.next());
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			// remove the current shop from every map but the current list
			// this iterator is iterating through
			removeShopIteratorFriendly(shop);
			
			// remove it from this list
			iterator.remove();
		}
		
	}
	
}
