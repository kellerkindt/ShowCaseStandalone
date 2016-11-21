/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-11-21 18:19 +01 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
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

package com.kellerkindt.scs.listeners;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.ShopHandler;
import me.minebuilders.clearlag.events.EntityRemoveEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Stops all {@link Item} belonging to a {@link ShopHandler}
 * to not be removed by {@link me.minebuilders.clearlag.Clearlag}
 *
 * @author Michael Watzko <michael at kellerkindt.com>
 */
public class ClearLagListener implements Listener {

    protected final ShowCaseStandalone scs;

    public ClearLagListener(ShowCaseStandalone scs, Plugin plugin) {
        this.scs = scs;
    }

    @EventHandler
    public void onEntityRemoveEvent(EntityRemoveEvent event) {
        ShopHandler  shopHandler = scs.getShopHandler();
        List<Entity> toRemove    = new ArrayList<>();

        for (Entity entity : event.getEntityList()) {
            if (entity instanceof Item) {
                if (shopHandler.isShopItem((Item)entity)) {
                    toRemove.add(entity);
                }
            }
        }

        event.getEntityList().removeAll(toRemove);
    }
}
