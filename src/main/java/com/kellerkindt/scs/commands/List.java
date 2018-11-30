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

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class List extends SimpleCommand {
    
    public static final String SEPERATOR    = ",";

    public List(ShowCaseStandalone scs, String...permissions) {
        super(scs, permissions, false, 1);
    }

    @Override
    public java.util.List<String> getTabCompletions(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        String                     player        = args[0];
        int                        ownerOf        = 0;
        int                        memberOf    = 0;
        java.util.List<String>    worlds        = new ArrayList<>();
        StringBuilder            sWorlds        = new StringBuilder();
        
        for (Shop p : scs.getShopHandler()) {
            boolean addWorld    = false;
            
            // check for owner
            if (p.isOwner(scs.getPlayerUUID(player))) {
                ownerOf++;
                addWorld    = true;
            }
            
            // check for member
            else if (p.isMember(scs.getPlayerUUID(player))) {
                memberOf++;
                addWorld    = true;
            }
            
            // get world name
            String    world    = p.getLocation().getWorld().getName();
            
            // add world name
            if (addWorld && !worlds.contains(world)) {
                worlds.add(world);
            }
        }
        
        
        boolean seperator = false;
        for (String s : worlds) {
            if (seperator)
                sWorlds.append(SEPERATOR);
            
            sWorlds.append(s);
            
            seperator = true;
        }
        
        scs.sendMessage(sender, Term.MESSAGE_LIST.get(player, ""+ownerOf, ""+memberOf, sWorlds.toString()));
    }
    
    
}
