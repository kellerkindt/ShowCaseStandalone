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

import java.util.UUID;

import com.earth2me.essentials.api.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.Balance;

import cosine.boseconomy.BOSEconomy;


public class BOSEconomyBalance extends NameBasedBalance implements Balance {

    private BOSEconomy          economy;
    
    public BOSEconomyBalance (ShowCaseStandalone scs, Plugin plugin) {
        super(scs);
        this.economy    = (BOSEconomy)plugin;
    }

    @Override
    public boolean isActive() {
        return economy.isEnabled();
    }

    @Override
    public boolean exists(OfflinePlayer player, UUID playerId, String playerName) {
        return (playerName = getPlayerName(player, playerId, playerName)) != null
            && Economy.playerExists(playerName);
    }

    @Override
    public boolean has(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        return (playerName = getPlayerName(player, playerId, playerName)) != null
            && economy.getPlayerMoneyDouble(playerName) >= amount;
    }

    @Override
    public boolean add(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        return (playerName = getPlayerName(player, playerId, playerName)) != null
            && economy.addPlayerMoney(playerName, amount, false);
    }

    @Override
    public boolean sub(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        return (playerName = getPlayerName(player, playerId, playerName)) != null
            && economy.addPlayerMoney(playerName, -amount, false);
    }

    @Override
    public String format(double amount) {
        return economy.getMoneyFormatted(amount);
    }
}
