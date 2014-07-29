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
package com.kellerkindt.scs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import com.kellerkindt.scs.interfaces.PriceRangeHandler;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
@SerializableAs(Properties.ALIAS_PRICERANGE)
public class PriceRange implements ConfigurationSerializable {
    
    public static final String KEY_MATERIAL    = "material";
    public static final String KEY_MIN        = "min";
    public static final String KEY_MAX        = "max";
    
    private PriceRangeHandler    handler;
    private Material            material;
    private double                min;
    private double                max;
    
    public PriceRange (PriceRangeHandler handler, Material material, double min, double max) {
        this.handler    = handler;
        this.material    = material;
        this.min        = min;
        this.max        = max;
    }
    
    public PriceRange (PriceRangeHandler handler, Material material, double max) {
        this(handler, material, 0, max);
    }
    
    public PriceRange (PriceRangeHandler handler, Material material) {
        this(handler, material, 0, Double.MAX_VALUE);
    }

    private PriceRange () {
        
    }
    
    /**
     * @return The {@link PriceRangeHandler} of this {@link PriceRange}
     */
    public PriceRangeHandler getPriceRangeHandler () {
        return handler;
    }
    
    
    /**
     * @param handler The {@link PriceRangeHandler} to set
     */
    public void setPriceRangeHandler (PriceRangeHandler handler) {
        this.handler    = handler;
    }
    

    /**
     * @return The min price for this range
     */
    public double getMin () {
        return getMin(true);
    }
    
    /**
     * @param limitByGlobalMin Whether to limit by the global min, or return the raw value for this {@link Material}
     * @return The min price
     */
    public double getMin (boolean limitByGlobalMin) {
        // check whether the global min is greater than this one
        if (handler.getGlobalMin() > min) {
            return handler.getGlobalMin();
        }
        
        return min;
    }
    
    /**
     * @return The max price for this range
     */
    public double getMax () {
        return getMax(true);
    }
    
    /**
     * @param limitByGlobalMax Whether to limit by the global max, or return the raw value for this {@link Material}
     * @return The max price
     */
    public double getMax (boolean limitByGlobalMax) {
        // check whether the global max is less than this one
        if (handler.getGlobalMax() < max) {
            return handler.getGlobalMax();
        }
        
        return max;
    }
    
    /**
     * @return The {@link Material} this {@link PriceRange} is for
     */
    public Material getMaterial () {
        return material;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put(KEY_MATERIAL,    material.toString());
        map.put(KEY_MIN,        min);
        map.put(KEY_MAX,        max);
        
        return map;
    }
    
    /**
     * @see ConfigurationSerializable
     */
    public static PriceRange deserialize (Map<String, Object> map) {
        // create the range
        PriceRange range = new PriceRange();
        
        range.material    = Material.getMaterial( (String)map.get(KEY_MATERIAL) );
        range.min        = (Double)    map.get(KEY_MIN);
        range.max        = (Double)    map.get(KEY_MAX);
        
        return range;
    }
}
