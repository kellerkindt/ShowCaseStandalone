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
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class VaultBalance implements Balance {
    
    private ShowCaseStandalone  scs;
    private Economy             economy;
    
    public VaultBalance (ShowCaseStandalone scs, Economy economy) {
        this.scs        = scs;
        this.economy    = economy;
    }

    @Override
    public boolean isActive() {
        return economy != null && economy.isEnabled();
    }

    @Override
    public boolean exists(OfflinePlayer player, UUID playerId, String playerName) {
        if (player != null) {
            return economy.hasAccount(player);
        }

        if (playerId != null) {
            return economy.hasAccount(
                    scs.getServer().getOfflinePlayer(playerId)
            );
        }

        return playerName != null && economy.hasAccount(playerName);
    }


    @Override
    public boolean has(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        if (player != null) {
            return economy.has(player, amount);
        }

        if (playerId != null) {
            return economy.has(
                    scs.getServer().getOfflinePlayer(playerId),
                    amount
            );
        }

        return playerName != null
            && economy.has(
                scs.getServer().getOfflinePlayer(playerName),
                amount
        );
    }
    @Override
    public boolean add(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        if (player != null) {
            return economy.depositPlayer(
                    player,
                    amount
            ).transactionSuccess();
        }

        if (playerId != null) {
            return economy.depositPlayer(
                    scs.getServer().getOfflinePlayer(playerId),
                    amount
            ).transactionSuccess();
        }

        return playerName != null
            && economy.depositPlayer(
                playerName,
                amount
            ).transactionSuccess();
    }

    @Override
    public boolean sub(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        if (player != null) {
            return economy.withdrawPlayer(
                    player,
                    amount
            ).transactionSuccess();
        }

        if (playerId != null) {
            return economy.withdrawPlayer(
                    scs.getServer().getOfflinePlayer(playerId),
                    amount
            ).transactionSuccess();
        }

        return playerName != null
            && economy.withdrawPlayer(
                playerName,
                amount
            ).transactionSuccess();
    }

    @Override
    public String format(double amount) {
        return economy.format(amount);
    }

    
    
}
