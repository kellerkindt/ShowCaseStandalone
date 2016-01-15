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
package com.kellerkindt.scs.interfaces;

import org.bukkit.Material;

import com.kellerkindt.scs.PriceRange;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public interface PriceRangeHandler extends Iterable<PriceRange>, ResourceDependent {
    
    /**
     * @return The amount of entries in this {@link PriceRangeHandler}
     */
    public int size();

    /**
     * @return The global max price
     */
    public double getGlobalMax ();
    
    /**
     * @return The global min price
     */
    public double getGlobalMin ();
    
    /**
     * @param max The global max price to set
     */
    public void setGlobalMax (double max);
    
    /**
     * @param min The global min price to set
     */
    public void setGlobalMin (double min);
    
    /**
     * @param material {@link Material} to get the {@link PriceRange} for
     * @return saveNew Whether to save the returned {@link PriceRange} if create for this request
     * @return A {@link PriceRange} representing the price range for the given {@link Material}
     */
    public PriceRange getRange (Material material, boolean saveNew);
    
    /**
     * Overwrites the {@link PriceRangeHandler}
     * @param range {@link PriceRange} to add
     */
    public void setRange (PriceRange range);
    
    /**
     * @param material The {@link Material} to get the min price for
     * @return The min price for the given {@link Material}, limited by the global min price
     */
    public double getMin (Material material);
    
    /**
     * @param material    {@link Material} to get the min price for
     * @param limitByGlobalMin Whether to limit by the global min, or return the raw value for this {@link Material}
     * @return The min price for the given {@link Material}, may be limited by the global price
     */
    public double getMin (Material material, boolean limitByGlobalMin);
    
    /**
     * @param material The {@link Material} to get the max price for
     * @return The max price for the given {@link Material}, may be limited by the global price
     */
    public double getMax (Material material);
    
    /**
     * @param material {@link Material} to get the max price for
     * @param limitByGlobalMax Whether to limit by the global max, or return the raw value for this {@link Material}
     * @return The max price for the given {@link Material}, may be limited by the global price
     */
    public double getMax (Material material, boolean limitByGlobalMax);
    
    /**
     * @param material {@link Material} to set the min price for
     * @param min The value to use as min price
     */
    public void setMin (Material material, double min);
    
    /**
     * @param material {@link Material} to set the max price for
     * @param max The value to use as max price
     */
    public void setMax (Material material, double max);
    
    /**
     * @param material {@link Material} to forget any range for
     */
    public void remove (Material material);
    
}
