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
package com.kellerkindt.scs.internals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.kellerkindt.scs.interfaces.Changeable;
import com.kellerkindt.scs.interfaces.StorageHandler;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.PlayerSession;
import com.kellerkindt.scs.SCSConfiguration;
import com.kellerkindt.scs.interfaces.PlayerSessionHandler;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class SimplePlayerSessionHandler implements PlayerSessionHandler {

    protected Map<UUID, PlayerSession>      sessions         = new HashMap<UUID, PlayerSession>();
    protected StorageHandler<PlayerSession> storageHandler;
    protected SCSConfiguration              configuration;
    
    public SimplePlayerSessionHandler (StorageHandler<PlayerSession> storageHandler, SCSConfiguration configuration) {
        this.storageHandler = storageHandler;
        this.configuration  = configuration;
    }

    @Override
    public void prepare() throws IOException {
        sessions.clear();
        for (PlayerSession session : storageHandler.loadAll()) {
            addSessionNotToStorage(session);
        }
    }

    /**
     * Will add the given {@link PlayerSession} to this {@link PlayerSessionHandler}
     * but won't notify the {@link StorageHandler} for it
     * @param session {@link PlayerSession} to add
     */
    protected void addSessionNotToStorage(PlayerSession session) {
        sessions.put(session.getPlayerId(), session);
        session.addChangeListener(changeListener);
    }

    @Override
    public Iterator<PlayerSession> iterator() {
        return sessions.values().iterator();
    }

    @Override
    public PlayerSession getSession(Player player) {
        return getSession(player, true);
    }

    
    @Override
    public PlayerSession getSession(Player player, boolean create) {
        return getSession(player.getUniqueId(), create);
    }

    @Override
    public PlayerSession getSession(UUID id) {
        return getSession(id, true);
    }

    @Override
    public PlayerSession getSession(UUID id, boolean create) {
        // get the session
        PlayerSession session = sessions.get(id);
        
        // does not exist yet + creation requested?
        if (session == null && create) {
            session = new PlayerSession(id);
            
            // defaults
            session.setShowTransactionMessage(configuration.getDefaultShowTransactionMessage());
            session.setUnitSize              (configuration.getDefaultUnit());

            addSessionNotToStorage(session);
            storageHandler.save(session);
        }
        
        return session;
    }

    @Override
    public void addSession(PlayerSession session) {
        addSession(session, true);
    }

    @Override
    public boolean addSession(PlayerSession session, boolean replace) {
        // set or replace --> replace allowed?
        if (!sessions.containsKey(session.getPlayerId()) || replace) {
            addSessionNotToStorage(session);
            storageHandler.save(session);
            return true;
        }
            
        return false;
    }

    @Override
    public int size() {
        return sessions.size();
    }

    @Override
    public boolean removeSession(Player player) {
        return removeSession(player.getUniqueId());
    }

    @Override
    public boolean removeSession(PlayerSession session) {
        return removeSession(session.getPlayerId());
    }

    @Override
    public boolean removeSession(UUID id) {
        PlayerSession session = sessions.remove(id);
        if (session != null) {
            session.removeChangeListener(changeListener);
            storageHandler.delete(session);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        for (PlayerSession session : sessions.values()) {
            storageHandler.delete(session);
        }
        sessions.clear();
    }

    protected Changeable.ChangeListener<PlayerSession> changeListener = new Changeable.ChangeListener<PlayerSession>() {
        @Override
        public void onChanged(PlayerSession changeable) {
            storageHandler.save(changeable);
        }
    };
}
