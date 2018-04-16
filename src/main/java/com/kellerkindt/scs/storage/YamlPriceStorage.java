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
package com.kellerkindt.scs.storage;

import com.kellerkindt.scs.PriceRange;
import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.interfaces.StorageHandler;
import com.kellerkindt.scs.internals.SimpleThreaded;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael <michael at email.com>
 */
public class YamlPriceStorage extends SimpleThreaded<YamlPriceStorage.Type, PriceRange> implements StorageHandler<PriceRange> {

    public static final String KEY_VERSION   = "version";
    public static final String KEY_GLOBALMIN = "pricerange.global.min";
    public static final String KEY_GLOBALMAX = "pricerange.global.max";
    public static final String KEY_RANGELIST = "pricerange.list";

    static {
        // register for deserialization
        ConfigurationSerialization.registerClass(PriceRange.class, Properties.ALIAS_PRICERANGE);
    }

    enum Type {
        SAVE,
        DELETE
    }
    
    protected File             file;
    protected List<PriceRange> loadedList = new ArrayList<PriceRange>();
    
    public YamlPriceStorage (Logger logger, File file) {
        super(logger);
        this.file = file;
    }


    @Override
    public PriceRange save(PriceRange element) {
        return enqueue(Type.SAVE, element);
    }

    @Override
    public void save(Iterable<PriceRange> collection) {
        enqueue(Type.SAVE, collection);
    }

    @Override
    public void delete(PriceRange element) {
        enqueue(Type.DELETE, element);
    }

    @Override
    protected void process(Entry entry) {
        switch(entry.request) {
            case SAVE:
                remove(entry.value); // replace similar entry
                loadedList.add(entry.value);
                save();
                entry.value.resetHasChanged();
                break;

            case DELETE:
                remove(entry.value);
                save();
                break;
        }
    }

    protected void remove(PriceRange range) {
        for (PriceRange r : loadedList) {
            if (Objects.equals(r.getMaterial(), range.getMaterial())) {
                loadedList.remove(r);
                return;
            }
        }
    }

    /**
     * Saves the current {@link #loadedList}
     */
    protected void save() {
        try {
            YamlConfiguration conf = new YamlConfiguration();

            conf.set(KEY_VERSION,   Properties.VERSION_STORAGE_PRICE);
            conf.set(KEY_RANGELIST, loadedList);
            conf.save(file);

        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Failed to save PriceRange", ioe);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<PriceRange> loadAll() throws IOException {
        if (!file.exists()) {
            // nothing to load
            return new ArrayList<PriceRange>(0);
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
        List<PriceRange>  list = new ArrayList<PriceRange>();

        int version = conf.getInt(KEY_VERSION, 0); // has been introduced with version being 1

        switch (version) {
            case 0:
                list.add(new PriceRange(null, null, conf.getDouble(KEY_GLOBALMIN), conf.getDouble(KEY_GLOBALMAX)));
        }

        if (conf.isList(KEY_RANGELIST)) {
            list.addAll((List<PriceRange>) conf.getList(KEY_RANGELIST));
        } else {
            logger.warning("'"+KEY_RANGELIST+"' seems not to be a valid list in "+file);
        }

        loadedList.clear();
        loadedList.addAll(list);

        return list;
    }

}
