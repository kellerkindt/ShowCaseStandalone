/**
* ShowCaseStandalone
* Copyright (C) 2014 Kellerkindt <copyright at kellerkindt.com>
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;

import com.kellerkindt.scs.PriceRange;
import com.kellerkindt.scs.interfaces.PriceRangeHandler;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class SimplePriceRangeHandler implements PriceRangeHandler {

    private Map<Material, PriceRange> map    = new HashMap<Material, PriceRange>();
    
    private double    globalMin    = 0;
    private double    globalMax    = Double.MAX_VALUE;

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
        return globalMax;
    }

    @Override
    public double getGlobalMin() {
        return globalMin;
    }

    @Override
    public void setGlobalMax(double max) {
        this.globalMax = max;
    }

    @Override
    public void setGlobalMin(double min) {
        this.globalMin = min;
    }

    @Override
    public PriceRange getRange(Material material) {
        // try to get it
        PriceRange range = map.get(material);
        
        // does not exist?
        if (range == null) {
            range = new PriceRange(this, material);
            // do not add to the map!
        }
        
        return range;
    }

    @Override
    public void setRange(PriceRange range) {
        map.put(range.getMaterial(), range);
        range.setPriceRangeHandler(this);
    }

    @Override
    public double getMin(Material material) {
        return getMin(material, true);
    }

    @Override
    public double getMin(Material material, boolean limitByGlobalMin) {
        return getRange(material).getMin(limitByGlobalMin);
    }

    @Override
    public double getMax(Material material) {
        return getMax(material, true);
    }

    @Override
    public double getMax(Material material, boolean limitByGlobalMax) {
        return getRange(material).getMax(limitByGlobalMax);
    }

    @Override
    public void setMin(Material material, double min) {
        // get the old one
        PriceRange range = getRange(material);
        
        // overwrite it
        map.put(material, new PriceRange(this, material, min, range.getMax(false)));
    }

    @Override
    public void setMax(Material material, double max) {
        // get the old one
        PriceRange range = getRange(material);
        
        // overwrite it
        map.put(material, new PriceRange(this, material, range.getMin(false), max));
    }

    @Override
    public void remove(Material material) {
        map.remove(material);
    }
    
}
