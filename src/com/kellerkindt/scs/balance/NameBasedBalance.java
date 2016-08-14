/*
 * ShowCaseStandalone
 * Copyright (c) 2016-08-14 16:05 +02 by Kellerkindt, <copyright at kellerkindt.com>
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
