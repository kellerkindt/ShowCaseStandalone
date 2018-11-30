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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.internals.SimpleThreaded;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kellerkindt.scs.PlayerSession;
import com.kellerkindt.scs.interfaces.StorageHandler;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class YamlPlayerSessionStorage extends SimpleThreaded<YamlPlayerSessionStorage.Type, PlayerSession> implements StorageHandler<PlayerSession> {

    public static final String PATH_LIST    = "PlayerSessionVariables";

    static {
        // register for deserialization
        ConfigurationSerialization.registerClass(PlayerSession.class, Properties.ALIAS_PLAYERSESSION);
    }

    enum Type {
        SAVE,
        DELETE
    }

    protected File                file;
    protected List<PlayerSession> loadedList = new ArrayList<>();
    
    public YamlPlayerSessionStorage (Logger logger, File file) {
        super(logger);

        this.file   = file;
    }

    @Override
    protected void process(Entry entry) {
        switch (entry.request) {
            case SAVE:
                loadedList.remove(entry.value);
                loadedList.add(entry.value);
                save();
                entry.value.resetHasChanged();
                break;

            case DELETE:
                if (loadedList.remove(entry.value)) {
                    save();
                }
                break;
        }
    }

    /**
     * Saves the current {@link #loadedList}
     */
    protected void save() {
        try {
            YamlConfiguration conf = new YamlConfiguration();

            // add it to the YAML file
            conf.set(PATH_LIST, loadedList);

            for (PlayerSession session : loadedList) {
                System.out.println(session);
            }


            // save it to disk
            conf.save(file);

        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Failed to save PlayerSessionStorage", ioe);
        }
    }

    @Override
    public PlayerSession save(PlayerSession element) {
        return enqueue(Type.SAVE, element);
    }

    @Override
    public void save(Iterable<PlayerSession> elements) {
        enqueue(Type.SAVE, elements);
    }

    @Override
    public void delete(PlayerSession element) {
        enqueue(Type.DELETE, element);
    }

    @Override
    public Collection<PlayerSession> loadAll() throws IOException {

        List<PlayerSession> sessions = new ArrayList<>();

        // nothing to load if the file does not exist
        if (file.exists()) {
            // load the file
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
            List<?>           list = conf.getList(PATH_LIST);

            for (Object o : list) {
                // well, just to be really sure :)
                if (o instanceof PlayerSession) {
                    sessions.add((PlayerSession)o);

                } else {
                    logger.severe("Unknown value in the PlayerSession file: "+o);
                }
            }
        }

        // update the current loaded state
        this.loadedList.clear();
        this.loadedList.addAll(sessions);

        return sessions;
    }
}
