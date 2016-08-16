/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-08-16 22:43 +02 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kellerkindt.scs.commands;

import java.util.List;
import java.util.logging.Level;

import com.kellerkindt.scs.shops.Shop;
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
        ShopHandler          handler = scs.getShopHandler();
        StorageHandler<Shop> storage = handler.getStorageHandler();
        
        try {
            
            scs.sendMessage(sender, Term.MESSAGE_RELOADING.get("config"));
            scs.reloadConfig();
            scs.loadSCSConfig(scs.getConfig());
            
            scs.sendMessage(sender, Term.MESSAGE_RELOADING.get("SCS"));
            
            scs.getLogger().info("Reloading SCS (command from " + sender.getName()+")");
            
            scs.getLogger().info("Removing display items");
            handler.hideAll();
            
            scs.getLogger().info("Writing changes to disk");
            storage.flush();
                
            scs.getLogger().info("Reloading shops from disk");
            handler.prepare();
            
            scs.getLogger().info("Showing display items in loaded chunks");
            handler.showAll();
            
        } catch (Exception ioe) {
            scs.getLogger().log(Level.SEVERE, "Couldn'T perform reload successfully", ioe);
            scs.sendMessage(sender, Term.ERROR_GENERAL.get("reloading") +ioe.getLocalizedMessage());
        }
    }
}
