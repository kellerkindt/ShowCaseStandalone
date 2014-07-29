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

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.Balance;

public class iConomy5Balance implements Balance {
    
    private iConomy             iconomy;
    private ShowCaseStandalone  scs;

    public iConomy5Balance(ShowCaseStandalone scs, Plugin plugin) {
        this.scs         = scs;
        this.iconomy     = (iConomy) plugin;
    }

    @Override
    public boolean hasEnough(Player p, double amount) {
        return hasEnough(p.getName(), amount);
    }
    
    
    @Override
    public boolean hasEnough(UUID id, double amount) {
        return hasEnough(scs.getPlayerName(id), amount);
    }
    
    public boolean hasEnough(String p, double amount) {
        try {
            return iConomy.getAccount(p).getHoldings().hasEnough(amount);
        } catch (NullPointerException npe) {
            scs.getLogger().warning("Player does not exist: "+p);
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return iconomy.isEnabled();
    }

    @Override
    public void add(Player p, double amount) {
        add(p.getName(), amount);
    }
    
    @Override
    public void add(UUID id, double amount) {
        add(scs.getPlayerName(id), amount);
    }

    private void add(String p, double amount) {
        try {
            iConomy.getAccount(p).getHoldings().add(amount);
        } catch (NullPointerException npe) {
            scs.getLogger().warning("Player does not exist: "+p);
        }
    }

    @Override
    public void sub(Player p, double amount) {
        sub(p.getName(), amount);
    }
    
    @Override
    public void sub(UUID id, double amount) {
        sub(scs.getPlayerName(id), amount);
    }

    private void sub(String p, double amount) {
        try {
            iConomy.getAccount(p).getHoldings().subtract(amount);
        } catch (NullPointerException npe) {
            scs.getLogger().warning("Player does not exist: "+p);
        }
    }

    @Override
    public String getClassName() {
        return iConomy.class.getName();
    }

    @Override
    public String format(double amount) {
        return iConomy.format(amount);
    }

}
