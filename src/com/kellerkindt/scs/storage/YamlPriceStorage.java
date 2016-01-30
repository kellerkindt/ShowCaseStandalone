/*
 * ShowCaseStandalone
 * Copyright (c) 2016-01-10 19:18 +01 by Kellerkindt, <copyright at kellerkindt.com>
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
