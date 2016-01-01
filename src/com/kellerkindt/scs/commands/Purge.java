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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Purge extends SimpleCommand {

    public static final String WILDCARD        = "*";
    public static final String PREFIX_WORLD    = "w:";
    public static final String PREFIX_UUID    = "u:";
    
    private Map<String, List<Shop>>    toDelete    = new HashMap<String, List<Shop>>();

    public Purge (ShowCaseStandalone scs, String...permissions) {
        super(scs, permissions, false, 1);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        
        List<String> list = new ArrayList<String>();
        
        if (args.length == 0 || args[0].startsWith(PREFIX_WORLD)) {
            for (World world : scs.getServer().getWorlds()) {
                list.add(PREFIX_WORLD+world.getName());
            }
        }
        
        // add all the player
        for (Player player : scs.getServer().getOnlinePlayers()) {
            list.add(player.getName());
        }
        
        return list;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        
        String  name        = args[1];
        String  world       = null;
        boolean ignoreWorld = false;
        boolean isUUID      = false;
        boolean isPlayer    = true;
        
        // delete whole world?
        if (name.startsWith(PREFIX_WORLD)) {
            name         = name.substring(PREFIX_WORLD.length());
            isPlayer    = false;
        }
        
        // by uuid?
        else if (name.startsWith(PREFIX_UUID)) {
            name        = name.substring(PREFIX_UUID.length());
            isPlayer    = false;
            isUUID        = true;
        }
        
        // load if already searched for
        List<Shop>        toRemove    = toDelete.get(name);
        
        // world was given
        if (args.length > 2) {
            world    = args[2];
        }
        
//        // console and no world was given
//        if (this.player == null && world == null && toRemove == null) {
//            Messaging.send(cs, Term.ERROR_MISSING_ARGUMENT_WORLD.get());
//            return true;
//        }
        
        // player and no world was given -> current world
        if (world == null && toRemove == null && sender instanceof Player) {
            world = ((Player)sender).getWorld().getName();
        }
        
        // wildcard?
        if (world == null || WILDCARD.equals(world)) {
            ignoreWorld = true;
        }
        
        // first call: find the shops
        if (toRemove == null) {
            toRemove    = new ArrayList<Shop>();
            
            for (Shop p : scs.getShopHandler()) {
                
                // get the world name safely
                String worldMatcher = !isUUID && p.getWorld() != null
                        ? p.getWorld().getName()
                        : isUUID
                            ? p.getWorldUUID().toString()
                            : null;
            
                if ( isPlayer && p.isOwner( scs.getPlayerUUID(name) ) && (ignoreWorld || world.equals(worldMatcher) )) {
                    toRemove.add(p);
                
                } else if ( !isPlayer && world.equals(worldMatcher) ) {
                    toRemove.add(p);
                }
                
            }
                
            
            if (toRemove.size() > 0)  {
                scs.sendMessage(sender, Term.MESSAGE_PURGE_FOUND.get(""+toRemove.size()));
                toDelete.put(name, toRemove);
            
            } else
                scs.sendMessage(sender, Term.ERROR_PURGE_ZERO_SHOPS.get());
            
        // second call: remove the shops
        } else {
            
            // delete them
            for (Shop p : toRemove)
                scs.getShopHandler().removeShop(p);
            
            // cleanup
            toDelete.put(name, null);
            
            scs.sendMessage(sender, Term.MESSAGE_PURGE_DELETED.get(""+toRemove.size(), name));
            
        }
        
    }
}
