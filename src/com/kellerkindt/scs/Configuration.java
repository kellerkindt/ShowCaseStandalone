/**
* ShowCaseStandalone
* Copyright (C) 2013 Kellerkindt <copyright at kellerkindt.com>
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import com.kellerkindt.scs.interfaces.ConfigurationChangedListener;

/**
 * @author kellerkindt <michael at kellerkindt.com>
 */
public abstract class Configuration {
    
    private FileConfiguration config;
    private Map<String, List<ConfigurationChangedListener>>    listeners = new HashMap<String, List<ConfigurationChangedListener>>();

    public Configuration (FileConfiguration config) {
        this.config = config;
    }
    
    /**
     * @return The {@link Set} of known keys for this configuration
     */
    public Set<String> getKeys () {
        return config.getKeys(true);
    }
    
    /**
     * Returns the value for the given key,
     * current value will be overwritten if it
     * isn't the same {@link Class} or is null
     * @param key    Key to the get the value for
     * @param alt    Alternative value to set and return
     * @return The current value or the given alternative value
     */
    @SuppressWarnings("unchecked") // try + catch --> it is save
    protected <T> T getForced (String key, T alt) {
        return getForced(key, alt, (Class<T>)alt.getClass());
    }
    
    @SuppressWarnings("unchecked") // try + catch --> it is save
    private <T> T getForced (String key, T alt, Class<T> clazz) {
        // get the current value
        Object value = getRaw(key);
        
        // is the value valid?
        if (value != null && clazz.isInstance(value)) {
            try {
                // if it is possible to cast, everything is fine
                return (T)value;
            } catch (ClassCastException cce) {}
        }
        
        // null or invalid data type -> overwrite
        update(key, alt, true);
        return alt;
    }
    
    /**
     * @param oldName Old name of the value
     * @param newName New name of the value
     */
    public void rename (String oldName, String newName) {
        Object value = config.get(oldName);
        config.set(oldName, null);
        config.set(newName, value);
    }
    
    /**
     * @param key Key to return the value for
     * @return The value for the given key or null
     */
    public Object getRaw (String key) {
        return config.get(key);
    }
    
    /**
     * Does set the new value for the given key,
     * if the key is known. Nothing will change
     * if the value is null
     * @param key    Key to set the value for
     * @param value    New value to set
     */
    public void update (String key, Object value) {
        update(key, value, false);
    }
        
    /**
     * Does set the new value for the given key,
     * if the key is known. Nothing will change
     * if the value is null
     * @param key    Key to set the value for
     * @param value    New value to set
     * @param ignoreUnset    Whether to set unknown keys
     */
    private void update (String key, Object value, boolean ignoreUnset) {
        if (ignoreUnset || (config.contains(key) && value != null)) {
            
            // get the old value
            Object old = getRaw(key);
            
            // set the new value
            config.set(key, value);
            
            // contact the listeners
            configurationChanged(key, value, old);            
        }
    }
    
    /**
     * @param dst Destination {@link File} to save to
     * @throws IOException
     */
    public void save (File dst) throws IOException {
        this.config.save(dst);
    }
    
    /**
     * Adds the given listeners, so it will be contacted,
     * if the value for the given key has been changed
     * @param key
     * @param listener
     */
    public void register (String key, ConfigurationChangedListener listener) {
        // get the list
        List<ConfigurationChangedListener> list = listeners.get(key);
        
        if (list == null) {
            list = new ArrayList<ConfigurationChangedListener>();
            listeners.put(key, list);
        }
        
        list.add(listener);
    }
    
    /**
     * To call, if a value has changed
     * @param key        Key thats value has changed
     * @param newValue    New value that has been set
     * @param oldValue    Old value
     */
    protected void configurationChanged (String key, Object newValue, Object oldValue) {
        if (listeners.containsKey(key)) {
            for (ConfigurationChangedListener listener : listeners.get(key)) {
                listener.onChanged(key, newValue, oldValue);
            }
        }
    }
}
