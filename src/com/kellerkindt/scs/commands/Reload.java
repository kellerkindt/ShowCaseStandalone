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

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.ShopHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Reload extends SimpleCommand {

	public Reload(ShowCaseStandalone scs, String...permissions) {
		super(scs, permissions, false);
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String[] args) {
		// nothing to do
		return null;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws CommandException {
		// disable first, then reload, then re-enable.
        ShopHandler 				sh 		= scs.getShopHandler();
        StorageHandler<ShopHandler>	storage	= scs.getShopStorageHandler();
        
        try {
        	
        	scs.sendMessage(sender, Term.MESSAGE_RELOADING.get("config"));
            scs.reloadConfig();
            scs.loadSCSConfig(scs.getConfig());
            
            scs.sendMessage(sender, Term.MESSAGE_RELOADING.get("SCS"));
            
            ShowCaseStandalone.slog(Level.INFO, "Reloading SCS (command from " + sender.getName()+")");
            ShowCaseStandalone.slog(Level.INFO, "Stopping shop update task.");
            sh.stop();
            
            ShowCaseStandalone.slog(Level.INFO, "Removing display items.");
	    	sh.hideAll();
	    	
	    	ShowCaseStandalone.slog(Level.INFO, "Writing changes to disk");
	    	storage.save(sh);
                
            ShowCaseStandalone.slog(Level.INFO, "Reloading shops from storage.");
            storage.load(sh);
            
            ShowCaseStandalone.slog(Level.INFO, "Starting shop update task.");
            sh.start();
            
            ShowCaseStandalone.slog(Level.INFO, "Showing display items in loaded chunks.");
            sh.showAll();
            
    	} catch (Exception ioe) {
    		ShowCaseStandalone.slog(Level.WARNING, "Exception on reload: " + ioe.getLocalizedMessage());
    		scs.sendMessage(sender, Term.ERROR_GENERAL.get("reloading") +ioe.getLocalizedMessage());
    	}
	}
}
