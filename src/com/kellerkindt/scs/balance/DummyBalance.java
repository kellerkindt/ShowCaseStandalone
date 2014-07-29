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

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.Balance;

public class DummyBalance implements Balance {
    
    public DummyBalance(ShowCaseStandalone scs) {
        
    }
    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String format(double amount) {
        return String.valueOf(amount);
    }
    
    @Override
    public boolean hasEnough(Player p, double amount) {
        return true;
    }
    
    @Override
    public boolean hasEnough(UUID id, double amount) {
        return true;
    }
    @Override public void add(Player p, double amount) { }
    @Override public void add(UUID id,     double amount) { }
    @Override public void sub(Player p, double amount) { }
    @Override public void sub(UUID id,     double amount) { }

}
