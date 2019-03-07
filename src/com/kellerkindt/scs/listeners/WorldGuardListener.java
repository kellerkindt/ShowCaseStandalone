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
package com.kellerkindt.scs.listeners;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseCreateEvent;
import com.kellerkindt.scs.exceptions.InsufficientPermissionException;
import com.kellerkindt.scs.utilities.Term;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class WorldGuardListener implements Listener {

    protected ShowCaseStandalone scs;
    protected WorldGuardPlugin   worldGuard;

    public WorldGuardListener(ShowCaseStandalone scs, Plugin wGuard) {
        this.scs = scs;

        if (wGuard instanceof WorldGuardPlugin) {
            worldGuard = (WorldGuardPlugin) wGuard;
        } else {
            throw new ClassCastException("Given Plugin is not WorldGuard");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onShopCreate(ShowCaseCreateEvent event) {
        if (!event.verify()) {
            // nothing to do
            return;
        }

        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Entered ShowCaseExecutingListener::onShowCaseCreateEvent");
        }

        // TODO update WG integration
        Location location = event.getShop().getLocation();
        Player   player   = event.getPlayer();

        RegionContainer manager     = WorldGuard.getInstance().getPlatform().getRegionContainer();
        LocalPlayer     localPlayer = worldGuard.wrapPlayer(player);

        boolean isAllowed = manager.createQuery().testState(
                BukkitAdapter.adapt(location),
                localPlayer,
                Flags.BUILD
        );

        if (!isAllowed) {
            event.setCancelled(true);
            event.setCause(new InsufficientPermissionException(
                    Term.ERROR_INSUFFICIENT_PERMISSION_REGION.get()
            ));

            if (scs.getConfiguration().isDebuggingShopCreation()) {
                scs.getLogger().info("Declined cause player is not allowed to build here");
            }
        }

        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Leaving ShowCaseExecutingListener::onShowCaseCreateEvent");
        }
    }
}
