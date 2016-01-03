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
package com.kellerkindt.scs.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import com.kellerkindt.scs.interfaces.Changeable;
import com.kellerkindt.scs.internals.NamedUUID;
import com.kellerkindt.scs.internals.SimpleThreaded;
import com.kellerkindt.scs.shops.*;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.ShopHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 *
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class YamlShopStorage extends SimpleThreaded implements StorageHandler<ShopHandler, Shop> {

    static {
        // register for deserialization
        ConfigurationSerialization.registerClass(BuyShop     .class);
        ConfigurationSerialization.registerClass(SellShop    .class);
        ConfigurationSerialization.registerClass(DisplayShop .class);
        ConfigurationSerialization.registerClass(ExchangeShop.class);
        ConfigurationSerialization.registerClass(NamedUUID   .class);
    }

    public static final String PATH_SHOP    = "shop";
    public static final String PATH_VERSION = "version";
    public static final String ENDING       = ".yml";

    protected final Changeable.ChangeListener<Shop> shopChangeListener = new Changeable.ChangeListener<Shop>() {
        @Override
        public void onChanged(Shop shop) {
            if (scs.getConfiguration().isDebuggingSave()) {
                scs.getLogger().info("Shop changed, going to enqueue save request for shop.id=" + shop.getId());
            }
            enqueueSaveRequest(shop);
        }
    };

    protected ShowCaseStandalone  scs        = null;
    protected File                shopDir    = null;
    protected final Queue<Shop>   toSave     = new ConcurrentLinkedQueue<Shop>();

    protected List<File>        faildToLoad  = new ArrayList<File>();
    
    public YamlShopStorage (ShowCaseStandalone scs, File shopDir) throws IOException {
        super();

        this.scs        = scs;
        this.shopDir    = shopDir;
        
        if (!shopDir.exists() && !shopDir.mkdirs()) {
            throw new IOException("Cannot access given directory: "+shopDir);
        }
    }

    @Override
    protected void run() {
        Shop shop;


        while (keepRunning()) {
            synchronized (toSave) {
                // try to get the shop to save
                shop = toSave.poll();

                if (shop == null) {
                    try {
                        toSave.notifyAll(); // notify flush
                        toSave.wait();      // wait for more work
                    } catch (InterruptedException ie) {
                        scs.getLogger().log(Level.WARNING, "Got interrupted, may cause performance issues", ie);
                    }
                    continue;
                }
            }


            if (scs.getConfiguration().isDebuggingSave()) {
                scs.getLogger().info("Going to save asynchronously, shop.id="+shop.getId());
            }
            saveSafely(shop);
        }
    }

    @Override
    public void flush() throws IOException {
        try {
            // there is no reason to wait, if the worker is no longer running
            if (isRunning()) {
                synchronized (toSave) {
                    while (toSave.size() > 0) {
                        toSave.wait();
                    }
                }
            }
        } catch (InterruptedException ie) {
            throw new IOException(ie);
        }
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
            scs.getLogger().log(Level.SEVERE, "Shop changed but unable to save changes!", t);
            return false;
        }
    }

    protected void enqueueSaveRequest(Shop shop) {
        synchronized (toSave) {
            // do not add it multiple times
            if (!toSave.contains(shop)) {
                toSave.add(shop);
                toSave.notifyAll();
            }
        }
    }

    @Override
    public void save(Shop shop) throws IOException {
        enqueueSaveRequest(shop);
    }

    protected void save(Shop shop, File dst) throws IOException {
        YamlConfiguration conf = new YamlConfiguration();

        // save
        conf.set(PATH_VERSION,  Properties.VERSION_STORAGE);
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
    public void loadAll(ShopHandler handler) throws IOException {
        // wait till everything has been written out
        flush();

        // list of loaded shops
        List<Shop> shops     = new ArrayList<Shop>();

        for (File file : shopDir.listFiles()) {
            try {
                YamlConfiguration conf = new YamlConfiguration();
                conf.load(file);

                // deserialize
                Shop     shop     = (Shop)conf.get(PATH_SHOP);
                int      version  =  conf.getInt  (PATH_VERSION, 6); // 6, since this was introduced at version 7

                if (shop == null) {
                    // also failed
                    faildToLoad.add(file);

                } else {
                    // add it
                    shops.add(shop);
                    shop.addChangeListener(shopChangeListener);
                }

            } catch (Throwable t) {
                faildToLoad.add(file);
                scs.getLogger().log(Level.SEVERE, "Couldn't load shop from file "+file.getAbsolutePath(), t);
            }
        }

        // add the mall to the handler
        handler.addAll(shops, true);
    }


    @Override
    public void saveAll(ShopHandler handler) throws IOException {

        List<File> toDelete = new ArrayList<File>();

        // gather files to delete
        for (File file : shopDir.listFiles()) {
            if (file.getName().endsWith(ENDING)) {
                toDelete.add(file);
            }
        }

        // do not delete a file if it failed to load
        toDelete.removeAll(faildToLoad);


        for (Shop shop : handler) {
            File  file  = getFile(shop.getId());

            // do not delete this file
            toDelete.remove(file);

            //do not save again, if the shop hasn't changed
            if (!shop.hasChanged()) {
                continue;
            }


            // actual save request
            enqueueSaveRequest(shop);
        }

        // delete files
        for (File file : toDelete) {
            file.delete();
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
