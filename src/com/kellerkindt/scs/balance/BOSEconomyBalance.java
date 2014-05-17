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

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.Balance;

import cosine.boseconomy.BOSEconomy;


public class BOSEconomyBalance implements Balance {
	
	private ShowCaseStandalone	scs;
	private BOSEconomy 			economy;
	
	public BOSEconomyBalance (ShowCaseStandalone scs, Plugin plugin) {
		this.scs		= scs;
		this.economy 	= (BOSEconomy)plugin;
	}
	
    @Override
	public String getClassName () {
		return economy.getClass().getName();
	}
	
    @Override
    public boolean hasEnough(UUID id, double amount) {
    	return hasEnough(scs.getPlayerName(id), amount);
    }
    
    @Override
    public boolean hasEnough(Player p, double amount) {
    	return hasEnough(p.getName(), amount);
    }
    
	private boolean hasEnough (String name, double amount) {
		return economy.getPlayerMoneyDouble(name) >= amount;
	}
	
    @Override
	public boolean isEnabled () {
		return economy.isEnabled();
	}
	
    @Override
	public void add (Player p, double amount) {
		add (p.getName(), amount);
	}
	
    @Override
	public void add (UUID id, double amount) {
    	add(scs.getPlayerName(id), amount);
	}
    
    private void add (String name, double amount) {
    	economy.addPlayerMoney(name, amount, false);
    }
        
       
	
    @Override
	public void sub (Player p, double amount) {
		sub(p.getName(), amount);
	}
	
    @Override
	public void sub (UUID id, double amount) {
		sub(scs.getPlayerName(id), amount);
	}
    
    private void sub (String name, double amount) {
    	economy.addPlayerMoney(name, -amount, false);
    }

    @Override
    public String format(double amount) {
        String currency = economy.getMoneyNamePlural();

        if(amount == 1) 
            currency = economy.getMoneyName();

        return amount + " " + currency;
    }
}
