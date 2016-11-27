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
package com.kellerkindt.scs.internals;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseItemSpawnEvent;
import com.kellerkindt.scs.events.ShowCaseOwnerSetEvent;
import com.kellerkindt.scs.events.ShowCaseShopHandlerChangedEvent;
import com.kellerkindt.scs.interfaces.Changeable;
import com.kellerkindt.scs.interfaces.ShopHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.ItemStackUtilities;
import com.kellerkindt.scs.utilities.Term;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;


public class SimpleShopHandler implements ShopHandler, Listener {


    protected HashMap<Item, Shop>             itemShops   = new HashMap<Item, Shop>();
    protected HashMap<Shop, Item>             shopItems   = new HashMap<Shop, Item>();
    protected HashMap<Block, Shop>            blockShops  = new HashMap<Block, Shop>();
    protected HashMap<UUID, Shop>             uuidShops   = new HashMap<UUID, Shop>();
    protected HashMap<UUID, Integer>          shopOwners  = new HashMap<UUID, Integer>();
    protected HashMap<Shop, List<ItemFrame>>  shopFrames  = new HashMap<Shop, List<ItemFrame>>();
    protected HashMap<ItemFrame, Shop>        frameShops  = new HashMap<ItemFrame, Shop>();
    protected ArrayList<Shop>                 shops       = new ArrayList<Shop>();    // for fast iteration

    protected List<Shop>                 visibleShops   = new LinkedList<Shop>();

    protected ShowCaseStandalone    scs           = null;
    protected boolean               fireEvents    = true;

    protected InternalShopChangeListener changeListener;
    protected StorageHandler<Shop>       storageHandler;


    public SimpleShopHandler(ShowCaseStandalone scs, StorageHandler<Shop> storageHandler) {
        this.scs            = scs;
        this.storageHandler = storageHandler;
        this.changeListener = new InternalShopChangeListener();

        scs.getServer().getPluginManager().registerEvents(this, scs);
        scs.getServer().getPluginManager().registerEvents(this.changeListener, scs);
    }

    @Override
    public void prepare() throws IOException {
        clear();
        addAll(storageHandler.loadAll());
    }

    @Override
    public StorageHandler<Shop> getStorageHandler() {
        return storageHandler;
    }

    @Override
    public void recheckShopShowState(Shop shop) {
        recheckShopShowState(Collections.singletonList(shop));
    }

    /**
     * @param shops {@link Iterable} of {@link Shop}s to check the show state for
     */
    protected void recheckShopShowState(Iterable<Shop> shops) {
        List<Shop> toShow = new LinkedList<Shop>();
        List<Shop> toHide = new LinkedList<Shop>();

        for (Shop shop : shops) {
            recheckShopShowState(shop, toShow, toHide);
        }

        // System.out.println("recheckShopShowState, toShow.size="+toShow.size()+", toHide.size="+toHide.size());

        for (Shop p : toHide) {
            hide(p);
        }

        for (Shop p : toShow) {
            show(p);
        }
    }

    /**
     * @param shop {@link Shop} to check whether to change the show state for
     * @param toShow {@link List} to add the shop to for a show request
     * @param toHide {@link List} to add the shop to for a hide request
     */
    protected void recheckShopShowState(Shop shop, List<Shop> toShow, List<Shop> toHide) {
        try {
            // regular getChunk().isLoaded() causes bukkit to load the chunk... (on getChunk())
            if (!isChunkLoaded(shop.getSpawnLocation())) {
                toHide.add(shop);
                return;
            }

            // shop Item
            Item item = shopItems.get(shop);

            // inactive? hide if configuration allows that
            if (scs.getConfiguration().isHidingInactiveShops() && (!shop.isActive() && shop.isVisible())) {
                toHide.add(shop);
            }

            // not visible yet?
            else if (item == null) {
                if (!scs.getConfiguration().isHidingInactiveShops() || shop.isActive()) {
                    // show it since it is active
                    toShow.add(shop);
                }
                else {
                    // more to remove the entries of the shop (frames...) than to remove it
                    toHide.add(shop);
                }

                // has been shown but item is now dead?
            } else if (item.isDead()) {
                if (!scs.getConfiguration().isHidingInactiveShops() || shop.isActive()) {
                    // remove it and show it again if active
                    toHide.add(shop);
                    toShow.add(shop);

                } else {
                    // inactive -> remove it
                    toHide.add(shop);
                }
            }
        } catch (Throwable t) {
            scs.getLogger().log(Level.SEVERE, "Error while handling shop="+shop, t);
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShopHandler#getShop(org.bukkit.entity.Item)
     */
    @Override
    public Shop getShop(Item item) {
        return itemShops.get(item);
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShopHandler#getShop(org.bukkit.block.Block)
     */
    @Override
    public Shop getShop(Block block) {
        return blockShops.get(block);
    }
    
    /**
     * @see com.kellerkindt.scs.interfaces.ShopHandler#getShop(java.util.UUID)
     */
    @Override
    public Shop getShop(UUID id) {
        return uuidShops.get(id);
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShopHandler#isShopItem(org.bukkit.entity.Item)
     */
    @Override
    public boolean isShopItem(Item item) {
        return itemShops.containsKey(item);
    }

    @Override
    public boolean isShopBlock(Block block) {
        return blockShops.containsKey(block);
    }

    private void addRaw(Shop p, boolean overwrite) {
        // if not already set a UUID, set it now
        if (p.getId() == null) {
            p.setId(UUID.randomUUID());
            scs.getLogger().info("Added shop without UUID, UUID set to "+p.getId());
        }
        
        // be sure that the UUID is unique
        while (!overwrite && uuidShops.containsKey(p.getId())) {
            p.setId(UUID.randomUUID());
        }
        
        if (overwrite && uuidShops.containsKey(p.getId())) {
            removeShop(uuidShops.get(p.getId()));
        }
        
        // add to lists
        blockShops.put(p.getBlock(),     p);
        uuidShops .put(p.getId(),     p);
        shops     .add(p);
        
        this.setFrames(p);
        this.incrementShopAmount( p.getOwnerId() );

        // get notified on changes
        p.addChangeListener(shopChangeListener);
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
     * @param shop     Shop to attach to
     * @param frame    Frame to attach
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
     * @param frame    ItemFrame to remove
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
    public void addShop(Shop shop) {
        addShop(shop, false);
    }
    
    @Override
    public void addShop(Shop p, boolean replace) {
        addRaw(p, replace);

        fireChangeEvent();
    }

    @Override
    public void removeShop(Shop shop) {
        // remove the main part
        removeShopIteratorFriendly(shop);
        
        // remove from iterator list
        shops.remove(shop);
        storageHandler.delete(shop);

        // notifications are no longer desired
        shop.removeChangeListener(shopChangeListener);
    }
    
    /**
     * Removes the Shop from every Map but the List for the iterator
     * This should allow the Iterator to remove shops while iterating
     * @param shop Shop to remove
     */
    public void removeShopIteratorFriendly (Shop shop) {
        // hide
        hide(shop);
    
        blockShops    .remove(shop.getBlock());
        uuidShops    .remove(shop.getId());
//        shops        .remove(shop);
        itemShops    .remove( shopItems    .remove(shop) );
    
        this.removeFrames(shop);
        this.decrementShopAmount( shop.getOwnerId() );
        
        fireChangeEvent();
    }


    @Override
    public int getShopAmount(UUID owner) {
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
     * @see com.kellerkindt.scs.interfaces.ShopHandler#showShopsFor(org.bukkit.Chunk)
     */
    @Override
    public void showShopsFor(Chunk chunk) {
        if (scs.getConfiguration().isDebuggingChunks()) {
            scs.getLogger().info("Load chunk: " + (chunk == null ? null : chunk.toString() + ", " + chunk.getWorld().getName()));
        }

        // fix for #263?
        if (chunk == null || chunk.getWorld() == null) {
            return;
        }

        try {
            for (Shop shop : this) {

                // ignore shops without a valid world
                if (shop.getWorld() == null) {
                    scs.getLogger().severe("Found showcase on not existing world! To remove perform: /scs purge u:" + shop.getWorldId());
                    continue;
                }


                if (isInChunk(shop.getSpawnLocation(), shop.getWorld(), chunk)) {
                    if (scs.getConfiguration().isDebuggingChunks()) {
                        scs.getLogger().info("Found shop to show: "+shop.getId());
                    }

                    // show if active
                    if (!scs.getConfiguration().isHidingInactiveShops() || shop.isActive()) {
                        show(shop);

                    } else {
                        // just be sure everything is removed
                        hide(shop);
                    }
                }
            }
        } catch (NullPointerException npe) {
            scs.getLogger().log(Level.SEVERE, "NPE on load chunk", npe);
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShopHandler#hideShopsFor(org.bukkit.Chunk)
     */
    @Override
    public void hideShopsFor(Chunk chunk) {
        if (scs.getConfiguration().isDebuggingChunks()) {
            scs.getLogger().info("Unload chunk: "+(chunk == null ? null : chunk.toString() + ", " + chunk.getWorld().getName()));
        }

        try {
            List<Shop> toHide = new ArrayList<Shop>();

            for (Shop shop : visibleShops) {

                if (shop.getWorld() == null) {
                    scs.getLogger().info("Found showcase in not existing world! To remove perform: /scs purge u:" + shop.getWorldId());
                    continue;
                }

                if (isInChunk(shop.getSpawnLocation(), shop.getWorld(), chunk)) {
                    if (scs.getConfiguration().isDebuggingChunks()) {
                        scs.getLogger().info("Found scs to unload: " + shop.getId());
                    }
                    
                    // hide
                    toHide.add(shop);
                }
            }

            for (Shop shop : toHide) {
                hide(shop);
            }

        } catch (NullPointerException npe) {
            scs.getLogger().log(Level.SEVERE, "NPE on unload chunk", npe);
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShopHandler#hideAll()
     */
    @Override
    public void hideAll() {
        for (Shop p : this) {
            hide(p);
        }
    }

    /**
     * @see com.kellerkindt.scs.interfaces.ShopHandler#showAll()
     */
    @Override
    public void showAll() {
        for (Shop p : this) {
            if (p.getBlock() != null) {
                if (isChunkLoaded(p.getLocation())) {
                    // System.out.println("showing shop, id="+p.getUUID()+", loc"+p.getLocation());
                    if (!scs.getConfiguration().isHidingInactiveShops() || p.isActive()) {
                        show(p);
                    }
                }
            }
        }
    }

    public boolean isInChunk(Location location, Chunk chunk) {
        return isInChunk(location, location.getWorld(), chunk);
    }

    public boolean isInChunk(Location location, World world, Chunk chunk) {
        return world.getName().equals(chunk.getWorld().getName())
            && getChunkX(location) == chunk.getX()
            && getChunkZ(location) == chunk.getZ();
    }

    public int getChunkZ(Location location) {
        return getChunkZ(location.getBlockZ());
    }

    public int getChunkZ(int blockZ) {
        return (int)Math.floor(blockZ / 16d);
    }

    public int getChunkX(Location location) {
        return getChunkX(location.getBlockX());
    }

    public int getChunkX(int blockX) {
        return (int)Math.floor(blockX / 16d);
    }

    /**
     * Checks whether the {@link Chunk} at the given
     * {@link Location} is loaded without loading it
     * 
     * @param loc {@link Location} to check
     * @return Whether the {@link Chunk} at the given {@link Location} is loaded
     */
    private boolean isChunkLoaded(Location loc) {
        int   cx = getChunkX(loc);
        int   cz = getChunkZ(loc);
        World cw = loc.getWorld();

        // fix for #263 ?
        if (cw == null) {
            return false;
        }

        // System.out.println("isChunkLoaded: "+loc+", cx="+cx+", cy="+cy+": "+cw.isChunkLoaded(cx, cy));
        return cw.isChunkLoaded(cx, cz);
    }

    /**
     * Removes any entry from this {@link ShopHandler},
     * no event invoking, no delegation to the {@link StorageHandler}
     */
    protected void clear() {
        itemShops   .clear();
        shopItems   .clear();
        blockShops  .clear();
        uuidShops   .clear();
        shopOwners  .clear();
        shopFrames  .clear();
        frameShops  .clear();
        shopFrames  .clear();
        visibleShops.clear();
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
        return new ShopIterator(shops.iterator());
    }
    
    @Override
    public void addAll(Collection<Shop> collection) {
        addAll(collection, false);
    }

    @Override
    public void addAll(Collection<Shop> shops, boolean replace) {
        // deactivate event firing
        fireEvents = false;

        // add shops
        for (Shop p : shops) {
            
            // is shop valid?
            if (p == null) {
                scs.getLogger().info("Ignoring broken shop");
                continue;
            }
            
            addShop(p, replace);
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
            if (!Objects.equals(event.getNewOwner().getId(), event.getShop().getOwnerId())) {

                // the old owner has now one shop less
                decrementShopAmount( event.getShop().getOwnerId() );
                
                // the new owner has now one shop more
                if (event.getNewOwner().getId() != null) {
                    incrementShopAmount(event.getNewOwner().getId());
                }
            }
        }
    }



    /**
     * @see com.kellerkindt.scs.interfaces.ShopHandler#hide(com.kellerkindt.scs.shops.Shop)
     */
    @Override
    public void hide(Shop shop) {
        // set invisible
        shop.setVisible(false);
        visibleShops.remove(shop);

        // get the Item for this shop
        Item item    = shopItems.get(shop);
        
        if (item != null) {
            
            // prepare teleportation to the ground
            int        x    = shop.getSpawnLocation().getBlockX();
            int     y     = 0;
            int     z    = shop.getSpawnLocation().getBlockZ();
            World    w    = shop.getSpawnLocation().getWorld();
            
            // teleport (fixed some client sided issues)
            item.teleport(new Location(w, x, y, z));

            // remove item
            item.remove();
            
            // remove item
            shopItems.remove(shop);
            itemShops.remove(item);
            
            
        }
        
        // Item Frame?
        List<ItemFrame>    frames    = shopFrames.get(shop);
        
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
                scs.getLogger().severe("Cannot display damaged shop, UUID="+shop.getId());
                return;
            }



            scs.callShowCaseEvent(new ShowCaseItemSpawnEvent(
                    null,
                    shop,
                    shop.getSpawnLocation()
            ), null);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onShowCaseItemSpawnEvent(ShowCaseItemSpawnEvent event) {

        // spawn new item
        Shop         shop             = event.getShop();
        Location     spawnLocation    = event.getLocation();
        ItemStack    itemStack        = shop.getItemStack().clone();

        if (scs.getConfiguration().isSpawningToMax()) {
            itemStack.setAmount(itemStack.getMaxStackSize());

        } else {
                /*
                 *  barrier 1, an amount of 0 does not cause any pickup event (seems to be so)
                 *  !! Mobs can pick the Item up, but do not drop it, since it has an amount of 0,
                 *     although they can use it ^^
                 */
            itemStack.setAmount(scs.getConfiguration().getSpawnCount());

            // since mc 1.11 an amount of zero does not seem to work anymore?
            if (itemStack.getAmount() == 0) {
                itemStack.setAmount(1);
            }
        }

        Item     item = shop.getWorld().dropItem(spawnLocation, itemStack);
        ItemMeta meta = item.getItemStack().getItemMeta();

        // TODO experimental
        if (scs.getConfiguration().isCustomNameVisible()) {
            String text = scs.getConfiguration().customNameFormat();
            String itemName = WordUtils.capitalize(item.getItemStack().getType().toString().toLowerCase().replaceAll("_", " "));
            if (meta != null && meta.hasDisplayName())
                itemName = meta.getDisplayName();
            text = text.replaceAll("%name%", itemName);
            text = text.replaceAll("%price%", Term.SIGN_PRICE.get(String.format("%.2f", shop.getPrice())));
            item.setCustomName(text);
            item.setCustomNameVisible(true);
        }

        // prevent item from being merged (at least in some cases)
        if (meta != null) {
            meta.setDisplayName(shop.getId().toString());
        }



        // System.out.println("droppedItem, Item-id: "+item.getEntityId()+", loc="+shop.getLocation()+", world="+shop.getWorld().getName());

        if (item.getItemStack().getType() == Material.STONE && shop.getItemStack().getType() != Material.STONE) {
            scs.getLogger().severe("Failed to drop Item (Item cannot be dropped), shop="+shop.getId()+", loc="+shop.getLocation());
            item.remove();
            return;
            // System.out.println("failure, original: "+shop.getItemStack()+", material="+shop.getItemStack().getType()+", meta="+shop.getItemStack().getItemMeta()+", loc="+shop.getLocation()+", world="+shop.getWorld().getName());
        }

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
        visibleShops.add(shop);
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
            
            // get Blocks
            Block blockUp     = shop.getBlock().getRelative(BlockFace.UP);
            Block blockDown    = shop.getBlock();

            Location locUp    = blockUp    .getLocation();
            Location locDown= blockDown    .getLocation();
            
            
            
            // Search the ItemFrame, TODO: find a faster / better solution
            for (ItemFrame frame : shop.getWorld().getEntitiesByClass(ItemFrame.class)) {

                BlockFace    face    = frame.getFacing();
                
                if (face == BlockFace.SOUTH || face == BlockFace.NORTH) {
                    face = face.getOppositeFace();
                }
                
                Block fBlock    = frame.getLocation().getBlock();
                Block aBlock    = fBlock.getRelative(face);
                
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
        double         maxYDiff    = 1.5;
        Item        shopItem    = shopItems.get(shop);
        
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
                   Item    item = (Item)e;
                   
                   // remove if not the current Item for this Shop
                   if (ItemStackUtilities.itemsEqual(item.getItemStack(), shop.getItemStack(), false) && shopItem != item)
                       item.remove();
               }
            }
        }
    }

    @Override
    public void addItemFrame(final ItemFrame frame) {
        
        // delayed, because the ItemFrame is added after this event
        scs.getServer().getScheduler().scheduleSyncDelayedTask(scs, new Runnable() {
            
            @Override
            public void run() {
                
                // blocks to check
                List<Block>    blocks        = new ArrayList<Block>();
                
                // get main blocks
                Block frameBlock         = frame.getLocation().getBlock();
                Block frameBlockDown    = frameBlock.getRelative(BlockFace.DOWN);
                
                // add them
                blocks.add(frameBlock);
                blocks.add(frameBlockDown);
                
                // allowed block faces
                List<BlockFace> faces     = new ArrayList<BlockFace>();
                
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
                        
                        Block     blockRelative     = block.getRelative(face);
                        Shop    shop            = getShop(blockRelative);
                        
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

    @Override
    public void removeItemFrame(ItemFrame frame) {
        
        final Shop shop    = frameShops.get(frame);
        
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
    
    protected Changeable.ChangeListener<Shop<?>> shopChangeListener = new Changeable.ChangeListener<Shop<?>>() {
        @Override
        public void onChanged(Shop<?> shop) {
            if (scs.getConfiguration().isDebuggingSave()) {
                scs.getLogger().info("Shop changed, going to enqueue save request for shop=" + shop);
            }
            storageHandler.save(shop);
        }
    };
    

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
        
        private Iterator<Shop>        iterator;
        private Shop                shop    = null;
        
        public ShopIterator (Iterator<Shop> iterator) {
            this.iterator    = iterator;
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
