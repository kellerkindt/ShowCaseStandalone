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
package com.kellerkindt.scs.interfaces;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.kellerkindt.scs.PlayerSession;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public interface PlayerSessionHandler extends Iterable<PlayerSession>, ResourceDependent {
    
    /**
     * @param player {@link Player} to return the session for
     * @return The session of the given {@link Player}
     */
    public PlayerSession getSession (Player player);
    
    /**
     * @param player {@link Player} to return the session for
     * @param create Whether to create the session if it does not exist yet
     * @return The session for the given {@link Player} or null
     */
    public PlayerSession getSession (Player player, boolean create);
    
    /**
     * @param id The {@link UUID} to return the session for
     * @return The session for the given {@link UUID}
     */
    public PlayerSession getSession (UUID id);
    
    /**
     * @param id The {@link UUID} to return the session for
     * @param create Whether to create the session if it does not exist yet
     * @return The session for the given {@link UUID} or null
     */
    public PlayerSession getSession (UUID id, boolean create);
    
    /**
     * Adds the given {@link PlayerSession}, a {@link PlayerSession}
     * with the same {@link UUID} will be replaced
     * @param session {@link PlayerSession} to add
     */
    public void addSession (PlayerSession session);
    
    /**
     * @param session    {@link PlayerSession} to add
     * @param replace    Whether to replace the current session, if already set
     * @return Whether the given {@link PlayerSession} has been set
     */
    public boolean addSession (PlayerSession session, boolean replace);
    
    /**
     * @param player {@link Player} to remove the {@link PlayerSession} for
     * @return Whether there was a {@link PlayerSession} set
     */
    public boolean removeSession (Player player);
    
    /**
     * @param session {@link Players} to remove
     * @return Whether hte {@link PlayerSession} was set
     */
    public boolean removeSession (PlayerSession session);
    
    /**
     * @param id {@link UUID} of the {@link PlayerSession} to remove
     * @return Whether there was a {@link PlayerSession} set
     */
    public boolean removeSession (UUID id);
    
    /**
     * @return The current size of this {@link PlayerSessionHandler}
     */
    public int size ();
    
    /**
     * Removes all {@link PlayerSession}s 
     */
    public void clear ();
}
