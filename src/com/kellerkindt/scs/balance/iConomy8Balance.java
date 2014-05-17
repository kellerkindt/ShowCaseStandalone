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
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.iCo6.iConomy;
import com.iCo6.system.Accounts;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.Balance;



public class iConomy8Balance implements Balance {
	private iConomy 			iconomy;
	private Accounts 			accounts;
	private ShowCaseStandalone	scs;
	
	public iConomy8Balance (ShowCaseStandalone scs, Plugin plugin) {
//		super (scs);
		this.iconomy	= (iConomy)plugin;
		this.scs		= scs;
		accounts 		= new Accounts ();
	}

	@Override
	public String getClassName() {
		return iconomy.getClass().getName();
	}
	
	@Override
	public boolean hasEnough(Player p, double amount) {
		return hasEnough(p.getName(), amount);
	}
	
	@Override
	public boolean hasEnough(UUID id, double amount) {
		return hasEnough(scs.getPlayerName(id), amount);
	}

	private boolean hasEnough(String p, double amount) {
		if (accounts.exists(p))
			return accounts.get(p).getHoldings().hasEnough(amount);
		else {
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] interacting failed with player "+p);
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
		if (accounts.exists(p))
			accounts.get(p).getHoldings().add(amount);
		else
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] interacting failed with player "+p);
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
		if (accounts.exists(p))
			accounts.get(p).getHoldings().subtract(amount);
		else
			scs.getServer().getLogger().log(Level.WARNING, "[ShowCaseStl] interacting failed with player "+p);
	}

        @Override
        public String format(double amount) {
            return iConomy.format(amount);
        }
	
	
}
