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
package com.kellerkindt.scs.internals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.kellerkindt.scs.PlayerSession;
import com.kellerkindt.scs.SCSConfiguration;
import com.kellerkindt.scs.interfaces.PlayerSessionHandler;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class SimplePlayerSessionHandler implements PlayerSessionHandler {

	private Map<UUID, PlayerSession> sessions 		= new HashMap<UUID, PlayerSession>();
	private SCSConfiguration		 configuration;
	
	public SimplePlayerSessionHandler (SCSConfiguration configuration) {
		this.configuration	= configuration;
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
			session.setShowTransactionMessage	(configuration.getDefaultShowTransactionMessage());
			session.setUnitSize					(configuration.getDefaultUnit());
			
			sessions.put(id, session);
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
		if (!sessions.containsKey(session.getUUID()) || replace) {
			sessions.put(session.getUUID(), session);
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
		return removeSession(session.getUUID());
	}

	@Override
	public boolean removeSession(UUID id) {
		return sessions.remove(id) != null;
	}

	@Override
	public void clear() {
		sessions.clear();
	}
}
