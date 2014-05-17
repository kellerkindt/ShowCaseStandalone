/**
* ShowCaseStandalone
* Copyright (C) 2014 Kellerkindt <copyright at kellerkindt.com>
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
package com.kellerkindt.scs.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.ShopHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Prune extends SimpleCommand {

	public Prune (ShowCaseStandalone scs, String...permissions) {
		super(scs, permissions);
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String[] args) {
		// nothing to do
		return null;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws CommandException {
		
		ShopHandler 				sh 			= scs.getShopHandler();
        StorageHandler<ShopHandler>	storage		= scs.getShopStorageHandler();
        
        try {
                scs.sendMessage(sender, Term.PRUNE.get());
                ShowCaseStandalone.slog(Level.INFO, "Backup.");
                List<Shop>	shops	= new ArrayList<Shop>();
                for (Shop p : sh)
                	shops.add(p);
                
                scs.log(Level.INFO, "Remove all shops from storage.", false);
                sh.removeAll();
                
                scs.log(Level.INFO, "Add backuped shops.", false);
                sh.addAll(shops);

                scs.log(Level.INFO, "Saving all currently loaded shops.", false);
                storage.save(sh);
                
    	} catch (Exception ioe) {
    		scs.log(Level.WARNING, "Exception on prune: " + ioe.getLocalizedMessage(), false);
    		scs.sendMessage(sender, Term.ERROR_GENERAL.get("pruning") + ioe.getLocalizedMessage());
    	}
	}
	
	
}
