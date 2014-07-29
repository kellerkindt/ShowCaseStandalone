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
package com.kellerkindt.scs.interfaces;

import java.util.UUID;

import org.bukkit.entity.Player;



public interface Balance {
    
    //public Balance (ShowCaseStandalone scs) {}
    public String  getClassName ();
    public boolean hasEnough (Player p, double amount);
    public boolean hasEnough (UUID  id, double amount);
    public boolean isEnabled ();
    public void add (Player p, double amount);
    public void add (UUID  id, double amount);
    public void sub (Player p, double amount);
    public void sub (UUID  id, double amount);
        public String format(double amount);
}
