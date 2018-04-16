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

public class DummyBalance implements Balance {
    
    public DummyBalance(ShowCaseStandalone scs) {
        
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean exists(OfflinePlayer player, UUID playerId, String playerName) {
        // can only exist if at least one parameter is not null
        return player != null || playerId != null || playerName != null;
    }

    @Override
    public boolean has(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        return true;
    }

    @Override
    public boolean add(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        return true;
    }

    @Override
    public boolean sub(OfflinePlayer player, UUID playerId, String playerName, double amount) {
        return true;
    }

    @Override
    public String format(double amount) {
        return String.format("%.2f", amount);
    }
}
