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

import com.kellerkindt.scs.PriceRange;
import com.kellerkindt.scs.interfaces.Changeable;
import com.kellerkindt.scs.interfaces.PriceRangeHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;
import org.bukkit.Material;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Michael <michael at kellerkindt.com>
 */
public class SimplePriceRangeHandler implements PriceRangeHandler {

    protected Logger                     logger;
    protected StorageHandler<PriceRange> storage;

    protected Map<Material, PriceRange>  map = new HashMap<Material, PriceRange>();

    public SimplePriceRangeHandler(Logger logger, StorageHandler<PriceRange> storageHandler) {
        this.logger  = logger;
        this.storage = storageHandler;
    }

    @Override
    public void prepare() throws IOException {
        map.clear();
        for (PriceRange range : storage.loadAll()) {
            addInternal(range);
        }
    }

    protected void addInternal(PriceRange range) {
        map.put(range.getMaterial(), range);
        range.setPriceRangeHandler(this);
        range.addChangeListener(changeListener);
    }

    @Override
    public int size() {
        return map.size();
    }
    
    @Override
    public Iterator<PriceRange> iterator() {
        return map.values().iterator();
    }

    @Override
    public double getGlobalMax() {
        return getRange(null, false).getMax(false);
    }

    @Override
    public double getGlobalMin() {
        return getRange(null, false).getMin(false);
    }

    @Override
    public void setGlobalMax(double max) {
        getRange(null, true).setMax(max);
    }

    @Override
    public void setGlobalMin(double min) {
        getRange(null, true).setMin(min);
    }

    @Override
    public PriceRange getRange(Material material, boolean saveNew) {
        // try to get it
        PriceRange range = map.get(material);
        
        // does not exist?
        if (range == null) {
            range = new PriceRange(this, material);
            // do not add to the map!

            if (saveNew) {
                addInternal(range);
                storage.save(range);
            }
        }
        
        return range;
    }

    @Override
    public void setRange(PriceRange range) {
        addInternal(range);
        storage.save(range);
    }

    @Override
    public double getMin(Material material) {
        return getMin(material, true);
    }

    @Override
    public double getMin(Material material, boolean limitByGlobalMin) {
        return getRange(material, false).getMin(limitByGlobalMin);
    }

    @Override
    public double getMax(Material material) {
        return getMax(material, true);
    }

    @Override
    public double getMax(Material material, boolean limitByGlobalMax) {
        return getRange(material, false).getMax(limitByGlobalMax);
    }

    @Override
    public void setMin(Material material, double min) {
        // get the old one
        getRange(material, true).setMin(min);
    }

    @Override
    public void setMax(Material material, double max) {
        // get the old one
        getRange(material, true).setMax(max);
    }

    @Override
    public void remove(Material material) {
        PriceRange range = map.remove(material);

        if (range != null) {
            storage.delete(range);
        }
    }

    protected Changeable.ChangeListener<PriceRange> changeListener = new Changeable.ChangeListener<PriceRange>() {
        @Override
        public void onChanged(PriceRange changeable) {
            storage.save(changeable);
        }
    };
}
