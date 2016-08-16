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

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.StorageHandler;
import com.kellerkindt.scs.internals.NamedUUID;
import com.kellerkindt.scs.internals.SimpleThreaded;
import com.kellerkindt.scs.shops.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 *
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class YamlShopStorage extends SimpleThreaded<YamlShopStorage.Type, Shop> implements StorageHandler<Shop> {

    static {
        // register for deserialization
        ConfigurationSerialization.registerClass(BuyShop     .class);
        ConfigurationSerialization.registerClass(SellShop    .class);
        ConfigurationSerialization.registerClass(DisplayShop .class);
        ConfigurationSerialization.registerClass(ExchangeShop.class);
        ConfigurationSerialization.registerClass(NamedUUID   .class);
    }

    enum Type {
        SAVE,
        DELETE
    }

    public static final String PATH_SHOP    = "shop";
    public static final String PATH_VERSION = "version";
    public static final String ENDING       = ".yml";


    protected ShowCaseStandalone  scs        = null;
    protected File                shopDir    = null;
    
    public YamlShopStorage (ShowCaseStandalone scs, File shopDir) throws IOException {
        super(scs.getLogger());

        this.scs        = scs;
        this.shopDir    = shopDir;
        
        if (!shopDir.exists() && !shopDir.mkdirs()) {
            throw new IOException("Cannot access given directory: "+shopDir);
        }
    }

    @Override
    protected void process(Entry entry) {
        switch (entry.request) {

            case SAVE:
                if (scs.getConfiguration().isDebuggingSave()) {
                    logger.info("Going to save asynchronously, shop="+entry.value);
                }
                saveSafely(entry.value);
                break;

            case DELETE:
                if (scs.getConfiguration().isDebuggingSave()) {
                    logger.info("Going to delete asynchronously, shop="+entry.value);
                }
                deleteSafely(entry.value);
                break;
        }
    }

    protected void deleteSafely(Shop shop) {
        if (!getFile(shop.getId()).delete()) {
            logger.warning("Failed to delete file for shop="+shop);
        }
    }


    @Override
    public Shop save(Shop shop) {
        return enqueue(Type.SAVE, shop);
    }

    /**
     * Saves the given {@link Shop} without throwing any exception,
     * but logging it to the console
     *
     * @param shop {@link Shop} to save
     * @return Whether the {@link Shop} has been saved successfully
     */
    protected boolean saveSafely(Shop shop) {
        try {
            save(shop, getFile(shop.getId()));
            return true;

        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Shop changed but unable to save changes!", t);
            return false;
        }
    }

    /**
     * Saves the given {@link Shop} to the given {@link File}
     * @param shop {@link Shop} to save
     * @param dst {@link File} to save to
     * @throws IOException On any error during saving
     */
    protected void save(Shop shop, File dst) throws IOException {
        YamlConfiguration conf = new YamlConfiguration();

        // save
        conf.set(PATH_VERSION,  Properties.VERSION_STORAGE_SHOP);
        conf.set(PATH_SHOP,     shop);

        // try to convert it, if it fails, original file won't be deleted
        String data = conf.saveToString();

        // save it
        FileWriter writer = new FileWriter(dst);

        writer.write(data);
        writer.flush();
        writer.close();

        // reset has changed
        shop.resetHasChanged();

    }

    @Override
    public Collection<Shop> loadAll() throws IOException {
        // wait till everything has been written out
        flush();

        // list of loaded shops
        List<Shop> shops  = new ArrayList<Shop>();
        File[]     files  = shopDir.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    YamlConfiguration conf = new YamlConfiguration();
                    conf.load(file);

                    // deserialize
                    Shop     shop     = (Shop)conf.get(PATH_SHOP);
                    int      version  =  conf.getInt  (PATH_VERSION, 6); // 6, since this was introduced at version 7

                    if (shop != null) {
                        shops.add(shop);
                    }

                } catch (Throwable t) {
                    logger.log(Level.SEVERE, "Couldn't load shop from file "+file.getAbsolutePath(), t);
                }
            }
        }


        return shops;
    }


    @Override
    public void save(Iterable<Shop> shops) {
        for (Shop shop : shops) {
            //do not save again, if the shop hasn't changed
            if (!shop.hasChanged()) {
                continue;
            }

            // actual save request
            enqueue(Type.SAVE, shop);
        }
    }

    @Override
    public void delete(Shop shop) {
        if (shop != null) {
            enqueue(Type.DELETE, shop);
        }
    }

    /**
     * @param uuid UUID to get the file for
     * @return The File for the given UUID
     */
    private File getFile (UUID uuid) {
        return new File (shopDir, uuid.toString() + ENDING);
    }


    
}
