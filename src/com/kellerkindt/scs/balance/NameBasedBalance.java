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

package com.kellerkindt.scs.balance;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.Balance;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author Michael <michael at kellerkindt.com>
 */
public abstract class NameBasedBalance implements Balance {

    protected final ShowCaseStandalone scs;

    public NameBasedBalance(ShowCaseStandalone scs) {
        this.scs = scs;
    }

    /**
     * @param player {@link OfflinePlayer} to get the name from or null
     * @param playerId {@link UUID} of the player to get the name for or null
     * @param playerName The name of the player to return or null
     * @return The name of the player for the given parameters or null
     */
    protected String getPlayerName(OfflinePlayer player, UUID playerId, String playerName) {
        if (playerName == null) {
            // need to retrieve the name, BOSEconomy only works with the name

            if (player == null) {
                // need to retrieve the OfflinePlayer since it has the players name

                if (playerId == null) {
                    // no information given at all, cannot work with that
                    return null;
                }

                player = scs.getServer().getOfflinePlayer(playerId);
            }

            if (player != null) {
                playerName = player.getName();
            }
        }

        return playerName;
    }
}
