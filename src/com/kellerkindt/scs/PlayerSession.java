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
package com.kellerkindt.scs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.internals.Transaction;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
@SerializableAs(Properties.ALIAS_PLAYERSESSION)
public class PlayerSession implements ConfigurationSerializable {
    
    // ------------- for serialization --------
    public static final String KEY_UUID                        = "uuid";
    public static final String KEY_LATESTTRANSACTION        = "latestTransaction";
    public static final String KEY_SHOWTRANSACTIONMESSAGE    = "showTransactionMessage";
    public static final String KEY_UNITSIZE                    = "unitSize";
    // -----------------------------------------

    private UUID                uuid;
    private Transaction            latestTransaction;
    private boolean             showTransactionMessage;
    private int                    unitSize;
    
    private PlayerSession () {
        
    }
    
    public PlayerSession (Player player) {
        this(player.getUniqueId());
    }
    
    public PlayerSession (UUID uuid) {
        this.uuid    = uuid;
    }
    
    /**
     * @return The {@link UUID} that is assigned to this {@link PlayerSession} (the players {@link UUID})
     */
    public UUID getUUID () {
        return uuid;
    }
    
    /**
     * Overrides the latest transaction
     * @param transaction Latest {@link Transaction} to set
     */
    public void setLatestTransaction (Transaction transaction) {
        this.latestTransaction    = transaction;
    }
    
    /**
     * @return The latest {@link Transaction}
     */
    public Transaction getLatestTransaction () {
        return latestTransaction;
    }
    
    /**
     * @param show Whether to show a transaction message
     */
    public void setShowTransactionMessage (boolean show) {
        this.showTransactionMessage = show;
    }
    
    /**
     * @return Whether to show a transaction message
     */
    public boolean showTransactionMessage () {
        return showTransactionMessage;
    }
    
    /**
     * @param size The unit size of this player
     */
    public void setUnitSize (int size) {
        this.unitSize = size;
    }
    
    /**
     * @return The unit size of this player
     */
    public int getUnitSize () {
        return unitSize;
    }
    
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put(KEY_UUID,                    uuid.toString());
        map.put(KEY_LATESTTRANSACTION,        latestTransaction);
        map.put(KEY_SHOWTRANSACTIONMESSAGE,    showTransactionMessage);
        map.put(KEY_UNITSIZE,                unitSize);
        
        return map;
    }
    
    /**
     * @see ConfigurationSerializable
     */
    public static PlayerSession deserialize (Map<String, Object> map) {
        
        PlayerSession psv = new PlayerSession();
        
        psv.uuid                    = UUID.fromString( (String)map.get(KEY_UUID) );
        psv.latestTransaction        = (Transaction) map.get(KEY_LATESTTRANSACTION);
        psv.showTransactionMessage    = (Boolean)        map.get(KEY_SHOWTRANSACTIONMESSAGE);
        psv.unitSize                = (Integer)        map.get(KEY_UNITSIZE);
        
        return psv;        
    }
}
