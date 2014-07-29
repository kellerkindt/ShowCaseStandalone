/**
* ShowCaseStandalone
* Copyright (C) 2012 Kellerkindt <copyright at kellerkindt.com>
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
package com.kellerkindt.scs.balance;

import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.Balance;

public class VaultBalance implements Balance {
    
    private ShowCaseStandalone  scs;
    private Economy             economy;
    
    public VaultBalance (ShowCaseStandalone scs, Economy economy) {
        this.scs        = scs;
        this.economy    = economy;
    }

    @Override
    public String getClassName() {
        return economy.getClass().getName();
    }
    
    @Override
    public boolean hasEnough(Player p, double amount) {
        return hasEnough(p.getUniqueId(), amount);
    }
    
    public boolean hasEnough(UUID id, double amount) {
        return economy.has(scs.getServer().getOfflinePlayer(id), amount);
    }

    @Override
    public boolean isEnabled() {
        return economy.isEnabled();
    }

    @Override
    public void add(Player p, double amount) {
        add(p.getUniqueId(), amount);
    }
    
    @Override
    public void add(UUID id, double amount) {
        economy.depositPlayer(scs.getServer().getOfflinePlayer(id), amount);
    }

    @Override
    public void sub(Player p, double amount) {
        sub(p.getUniqueId(), amount);
    }
    
    @Override
    public void sub(UUID id, double amount) {
        economy.withdrawPlayer(scs.getServer().getOfflinePlayer(id), amount);
    }

    @Override
    public String format(double amount) {
        return economy.format(amount);
    }

    
    
}
