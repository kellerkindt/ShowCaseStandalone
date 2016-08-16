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

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.Balance;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.UUID;

public class EssentialsBalance extends NameBasedBalance implements Balance {

    private Essentials essentials;
    
    public EssentialsBalance (ShowCaseStandalone scs, Plugin plugin) {
        super(scs);
        this.essentials     = (Essentials)plugin;
    }

    @Override
    public boolean isActive() {
        return essentials.isEnabled();
    }

    @Override
    public boolean exists(OfflinePlayer player, UUID playerId, String playerName) {
        return (playerName = getPlayerName(player, playerId, playerName)) != null
            && Economy.playerExists(playerName);
    }

    @Override
    public boolean has(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        try {
            return (playerName = getPlayerName(player, playerId, playerName)) != null
                && Economy.hasEnough(playerName, new BigDecimal(amount));
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public boolean add(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        try {
            if ((playerName = getPlayerName(player, playerId, playerName)) != null) {
                Economy.add(playerName, new BigDecimal(amount));
                return true;
            }
            return false;
        } catch (UserDoesNotExistException e) {
            return false;
        } catch (NoLoanPermittedException e) {
            return false;
        }
    }

    @Override
    public boolean sub(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        try {
            if ((playerName = getPlayerName(player, playerId, playerName)) != null) {
                // dude... typo :P
                Economy.substract(playerName, new BigDecimal(amount));
                return true;
            }
            return false;
        } catch (UserDoesNotExistException e) {
            return false;
        } catch (NoLoanPermittedException e) {
            return false;
        }
    }

    @Override
    public String format(double amount) {
        return Economy.format(new BigDecimal(amount));
    }

    
}
