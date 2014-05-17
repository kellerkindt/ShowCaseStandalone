/**
* ShowCaseStandalone
* Copyright (C) 2013 Kellerkindt <copyright at kellerkindt.com>
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
package com.kellerkindt.scs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCasePlayerBuyEvent;
import com.kellerkindt.scs.events.ShowCasePlayerExchangeEvent;
import com.kellerkindt.scs.events.ShowCasePlayerSellEvent;
import com.kellerkindt.scs.utilities.MaterialNames;
import com.kellerkindt.scs.utilities.Term;
import com.mciseries.iLogTransactions.TransactionEntry;

/**
 *
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class ILogTransactionListener implements Listener {
	
	private ShowCaseStandalone scs;
	
	public ILogTransactionListener (ShowCaseStandalone scs) {
		this.scs	= scs;
	}
	

	@EventHandler (priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerBuyEvent (ShowCasePlayerBuyEvent event) {
		// gather information
		int			quantity 	= event.getQuantity();
		double		price		= event.getShop().getPrice();
		ItemStack	stack		= event.getShop().getItemStack();
		
		// log
		new TransactionEntry(scs.getName(), quantity, false, event.getPlayer().getName(),
				Term.MESSAGE_SELL_COSTUMER.get(MaterialNames.getItemName(stack), ""+quantity, ""+price));
	}
	
	@EventHandler (priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerSellEvent (ShowCasePlayerSellEvent event) {
		// gather information
		int			quantity 	= event.getQuantity();
		double		price		= event.getShop().getPrice();
		ItemStack	stack		= event.getShop().getItemStack();
		
		// log
		new TransactionEntry(scs.getName(), quantity, true, event.getPlayer().getName(),
				Term.MESSAGE_BUY.get(MaterialNames.getItemName(stack), ""+quantity, ""+price));
		
	}
	
	@EventHandler (priority=EventPriority.MONITOR, ignoreCancelled=true)
	public  void onPlayerExchangeEvent (ShowCasePlayerExchangeEvent event) {
		// no transaction to log
	}
}
