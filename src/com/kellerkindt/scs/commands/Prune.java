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

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.interfaces.ShopHandler;
import com.kellerkindt.scs.interfaces.StorageHandler;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

        ShopHandler                         sh      = scs.getShopHandler();
        StorageHandler<ShopHandler, Shop>   storage = scs.getShopStorageHandler();
        
        try {
                scs.sendMessage(sender, Term.PRUNE.get());
                scs.getLogger().info("Backup");
                List<Shop>    shops    = new ArrayList<Shop>();
                for (Shop p : sh)
                    shops.add(p);
                
                scs.getLogger().info("Remove all shops from storage.");
                sh.removeAll();
                
                scs.getLogger().info("Add backuped shops.");
                sh.addAll(shops);

                scs.getLogger().info("Saving all currently loaded shops.");
                storage.saveAll(sh);
                
        } catch (Exception ioe) {
            scs.getLogger().log(Level.SEVERE, "Couldn't perform prune successfully", ioe);
            scs.sendMessage(sender, Term.ERROR_GENERAL.get("pruning") + ioe.getLocalizedMessage());
        }
    }
    
    
}
