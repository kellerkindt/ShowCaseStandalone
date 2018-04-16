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
import com.kellerkindt.scs.events.ShowCaseItemSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.originmc.fbasics.FBasics;
import org.originmc.fbasics.settings.AntiGlitchSettings;
import org.originmc.fbasics.settings.Settings;

/**
 * @author Michael <michael at kellerkindt.com>
 *
 * This listener disables the {@link org.originmc.fbasics.listener.InventoryDupeListener} for the time
 * of an {@link ItemSpawnEvent} if the spawned {@link org.bukkit.entity.Item} is either from a
 * {@link com.kellerkindt.scs.shops.Shop} or a {@link com.kellerkindt.scs.shops.Shop}
 * is going to be created ({@link ShowCaseCreateEvent}).
 *
 * This is a workaround, since there is no other way to mark an event {@link ItemSpawnEvent}
 * to not be cancelled by {@link org.originmc.fbasics.listener.InventoryDupeListener}
 */
public class FBasicsAntiInventoryDupeListener implements Listener {

    protected ShowCaseStandalone scs;
    protected boolean debug;

    protected FBasics            fBasics;
    protected AntiGlitchSettings settings;

    protected Boolean before;

    public FBasicsAntiInventoryDupeListener(ShowCaseStandalone scs, Plugin plugin) {
        this.scs        = scs;
        this.fBasics    = (FBasics)plugin;

        this.debug      = scs.getConfiguration().isDebuggingShopCreation();
        // not loaded yet, throws NPE
        // this.settings   = this.fBasics.getSettings().getAntiGlitchSettings();
    }

    /**
     * @return Whether {@link FBasics} is preventing 'InventoryDupe'
     */
    protected boolean isFBasicsInventoryDupe() {
        if (settings == null) {
            Settings settings = fBasics.getSettings();

            if (settings != null) {
                this.settings = settings.getAntiGlitchSettings();
            }
        }
        return settings != null && settings.isInventoryDupe();
    }

    /**
     * @param value Whether {@link FBasics} is supposed to prevent 'InventoryDupe'
     */
    protected void setFBasicsInventoryDupe(boolean value) {
        if (settings != null) {
            // allow setting only, if successfully read before
            settings.setInventoryDupe(value);
        }
    }

    protected void debug(String msg) {
        if (debug) {
            scs.getLogger().info(msg);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onShowCaseItemSpawnEventBeforeAntiInventoryDupeListener(ShowCaseItemSpawnEvent event) {
        debug("onShowCaseItemSpawnEventBeforeAntiInventoryDupeListener, isInventoryDupe="+isFBasicsInventoryDupe());
        this.before = isFBasicsInventoryDupe();
        setFBasicsInventoryDupe(false);
        debug(" disabled FBasicsInventoryDupe");
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onShowCaseItemSpawnEventAfterAntiInventoryDupeListener(ShowCaseItemSpawnEvent event) {
        debug("onShowCaseItemSpawnEventAfterAntiInventoryDupeListener, before="+isFBasicsInventoryDupe());
        if (before != null) {
            setFBasicsInventoryDupe(before);
            debug(" restored FBasicsInventoryDupe="+ before);
            before = null;
        }
    }
}
