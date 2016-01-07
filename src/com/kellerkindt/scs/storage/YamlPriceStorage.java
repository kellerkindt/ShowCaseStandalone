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
package com.kellerkindt.scs.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.internals.SimpleThreaded;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kellerkindt.scs.PriceRange;
import com.kellerkindt.scs.interfaces.PriceRangeHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 *
 * @author michael <michael at email.com>
 */
public class YamlPriceStorage extends SimpleThreaded implements StorageHandler<PriceRangeHandler, PriceRange> {
    
    public static final String KEY_GLOBALMIN    = "pricerange.global.min";
    public static final String KEY_GLOBALMAX    = "pricerange.global.max";
    public static final String KEY_RANGELIST    = "pricerange.list";

    static {
        // register for deserialization
        ConfigurationSerialization.registerClass(PriceRange.class, Properties.ALIAS_PRICERANGE);
    }
    
    private File file;
    
    public YamlPriceStorage (File file) {
        this.file = file;
    }

    @Override
    protected void run() {
        // TODO
    }

    @Override
    public void flush() throws IOException {
        // TODO
    }

    @Override
    public void save(PriceRange element) throws IOException {
        // TODO
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadAll(PriceRangeHandler handler) throws IOException {
        // nothing to do if the file dos not exist
        if (!file.exists()) {
            return;
        }
        
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
        
        handler.setGlobalMin( conf.getDouble(KEY_GLOBALMIN) );
        handler.setGlobalMax( conf.getDouble(KEY_GLOBALMAX) );
        
        for (PriceRange range : ((List<PriceRange>)conf.getList(KEY_RANGELIST))) {
            handler.setRange(range);
        }
    }

    @Override
    public void saveAll(PriceRangeHandler handler) throws IOException {
        YamlConfiguration conf = new YamlConfiguration();
        
        conf.set(KEY_GLOBALMIN, handler.getGlobalMin());
        conf.set(KEY_GLOBALMAX, handler.getGlobalMax());
        
        List<PriceRange> list = new ArrayList<PriceRange>();
        
        for (PriceRange range : handler) {
            list.add(range);
        }
        
        conf.set(KEY_RANGELIST, list);
        conf.save(file);
    }

}
