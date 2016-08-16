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
package com.kellerkindt.scs.interfaces;


import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import com.kellerkindt.scs.internals.NamedUUID;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;

import com.kellerkindt.scs.shops.Shop;




public interface ShopHandler extends Iterable<Shop>, ResourceDependent {
    
        
    /**
     * If a {@link Shop} is shown, there'll be an {@link Item}
     * dropped for it. If you pass one of these {@link Item}s here,
     * it'll return the {@link Shop} it has been dropped for
     *
     * @param item {@link Item} to get the {@link Shop} for
     * @return The {@link Shop} for the given {@link Item} or null
     */
    Shop getShop(Item item);
    
    /**
     * Every {@link Shop} has a {@link Block}-location,
     * if you pass that {@link Block} here, you it'll
     * return the {@link Shop} that exists on the given
     * {@link Block}-location
     *
     * @param block {@link Block} to get the {@link Shop} for
     * @return The {@link Shop} for the given {@link Block} or null
     */
    Shop getShop(Block block);
    
    /**
     * Every {@link Shop} has a {@link UUID}. This method
     * will return the {@link Shop} known to this {@link ShopHandler}
     * that has the given {@link UUID}
     *
     * @param id {@link UUID} of the {@link Shop} to return
     * @return The {@link Shop} for the given {@link UUID} or null
     */
    Shop getShop(UUID id);
    
    /**
     * If a {@link Shop} is shown, there'll be an {@link Item}
     * dropped for it. If you pass one of these {@link Item}s
     * here, it'll return true.
     *
     * @param item {@link Item} to check
     * @return Whether the given {@link Item} has been dropped for a {@link Shop}
     */
    boolean isShopItem(Item  item);
    
    /**
     * Every {@link Shop} has a {@link Block}-location,
     * if you pass a {@link Block}-location a {@link Shop}
     * exists for, it'll return true.
     *
     * @param block {@link Block} to check
     * @return Whether there exists a {@link Shop} for the given {@link Block}
     */
    boolean isShopBlock(Block block);
    
    /**
     * Might update the {@link Shop}'s {@link UUID},
     * if there exists another {@link Shop} with the
     * same {@link UUID} already
     * @param shop {@link Shop} to add
     */
    void addShop(Shop shop);
    
    /**
     * Adds the given {@link Shop} to this {@link ShopHandler}.
     * If {@code replace} is false, and there exists already
     * a {@link Shop} with the {@link UUID} of the given {@link Shop},
     * the given's {@link Shop} {@link UUID} will be changed.
     * If {@code replace} is true, the already existing {@link Shop},
     * would be replace by the given {@link Shop}
     *
     * @param shop {@link Shop} to add
     * @param replace Whether to replace an already existing {@link Shop} with the same {@link UUID}
     */
    void addShop(Shop shop, boolean replace);
    
    /**
     * Will add all {@link Shop}s in the given {@link Collection}
     * by calling {@link #addShop(Shop)} for each.
     *
     * @param collection {@link Collection} of {@link Shop}s to add
     */
    void addAll(Collection<Shop> collection);
    
    /**
     * Will add all {@link Shop}s in the given {@link Collection},
     * by calling {@link #addShop(Shop, boolean)} for each.
     *
     * @param collection  {@link Collection} of {@link Shop}s to add
     * @param replace See {@link #addShop(Shop, boolean)}
     */
    void addAll(Collection<Shop> collection, boolean replace);
    
    /**
     * @param shop {@link Shop} to remove
     */
    void removeShop(Shop shop);
    
    /**
     * Removes all {@link Shop}s
     */
    void removeAll ();
    
    /**
     * @param owner {@link UUID} of the owner
     * @return The amount of {@link Shop}s the given player owns
     */
    int getShopAmount(UUID owner);
    

    /**
     * @param chunk {@link Chunk} to {@link #hide(Shop)} {@link Shop}s for
     */
    void showShopsFor(Chunk chunk);
    
    /**
     * @param chunk {@link Chunk} to {@link #show(Shop)} {@link Shop}s for
     */
    void hideShopsFor(Chunk chunk);
    
    /**
     * Hides all {@link Shop}s
     */
    void hideAll();
    
    /**
     * Shows all {@link Shop}s in loaded {@link Chunk}s
     */
    void showAll();

    /**
     * @param shop {@link Shop} to show
     */
    void show(Shop shop);

    /**
     * @param shop {@link Shop} to hide
     */
    void hide(Shop shop);

    /**
     * @return The {@link StorageHandler} of this {@link ShopHandler}
     */
    StorageHandler<Shop> getStorageHandler();
    
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
     * @param shop {@link Shop} to check whether to change its show state
     */
    void recheckShopShowState(Shop shop);
    
    /**
     * @return The size of this {@link ShopHandler, which is determined by the amount of {@link Shop}s in this {@link ShopHandler}
     */
    int size();
}
