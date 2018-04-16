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

package com.kellerkindt.scs.events;

import com.kellerkindt.scs.shops.Shop;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * @author Michael <michael at kellerkindt.com>
 *
 * This event cannot be cancelled
 */
public class ShowCaseItemSpawnEvent extends ShowCaseShopEvent {

    protected Location location;

    public ShowCaseItemSpawnEvent(Player player, Shop shop, Location location) {
        super(player, shop);

        this.location = location;
    }

    /**
     * @return The {@link Location} the {@link Item} is spawned
     */
    public Location getLocation() {
        return location;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        // cannot be cancelled
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
