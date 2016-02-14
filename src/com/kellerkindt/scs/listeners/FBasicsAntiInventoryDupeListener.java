/*
 * ShowCaseStandalone
 * Copyright (c) 2016-02-14 15:56 +01 by Kellerkindt, <copyright at kellerkindt.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
