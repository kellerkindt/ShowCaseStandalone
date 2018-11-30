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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Repair extends SimpleCommand {
    
    public static final String          RESTORE     = "restore";
    public static final String          DELETE      = "delete";
    public static final List<String>    LIST_TAB    = Arrays.asList(RESTORE, DELETE);

    public Repair (ShowCaseStandalone scs, String...permissions) {
        super(scs, permissions);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        
        List<String> list = new ArrayList<>();
        
        for (String s : LIST_TAB) {
            if (args.length == 0 || s.toLowerCase().startsWith(args[0].toLowerCase())) {
                list.add(s);
            }
        }
        
        return list;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        
        Set<Shop> restoreable     = new HashSet<>();
        Set<Shop> deleteable     = new HashSet<>();
        
        //prepare
        for ( Shop shop : this.scs.getShopHandler() ) {
            //get blocks
            Block shopBlock = shop.getBlock();
            Block itemBlock = shopBlock.getWorld().getBlockAt( shopBlock.getX(), shopBlock.getY() + 1, shopBlock.getZ() );
            
            //decide what to do with a shop
            if ( shopBlock.getType() == Material.AIR && itemBlock.getType() != Material.AIR ) {
                deleteable.add( shop );
            } else if ( shopBlock.getType() == Material.AIR ) {
                restoreable.add( shop );
            } else if ( itemBlock.getType() != Material.AIR ) {
                deleteable.add( shop );
            }
            
        }
        
        if ( args.length < 1 ) {
            //output
            scs.sendMessage(sender, Term.REPAIR_INFO    .get( restoreable.size() + restoreable.size() + deleteable.size() + "" ) );
            scs.sendMessage(sender, Term.REPAIR_HELP_1    .get( restoreable.size() + "" ) );
            scs.sendMessage(sender, Term.REPAIR_HELP_2    .get( deleteable.size()  + "" ) );
        }
        
        else if ( args[0].equalsIgnoreCase( RESTORE ) ) {
            //output
            scs.sendMessage(sender, Term.REPAIR_INFO    .get( restoreable.size() + "" ) );
            scs.sendMessage(sender, Term.RESTORE_START    .get() );
            
            //restore
            for ( Shop shop : restoreable ) {
                shop.getBlock().setType( Material.BEDROCK );
            }
            
            // items may moved after block was set
            scs.getShopHandler().hideAll();
            scs.getShopHandler().showAll();
            
            //output
            scs.sendMessage(sender, Term.RESTORE_END    .get( restoreable.size() + "" ) );
            
        } else if ( args[0].equalsIgnoreCase( DELETE ) ) {
            //output
            scs.sendMessage(sender, Term.REPAIR_INFO    .get( deleteable.size() + "" ) );
            scs.sendMessage(sender, Term.DELETE_START    .get() );
            
            //delete
            for ( Shop shop : deleteable ) {

//                shop.hide();    // done in remove
                this.scs.getShopHandler().removeShop( shop );
            }
            
            //output
            scs.sendMessage(sender, Term.DELETE_END        .get( deleteable.size() + "" ) );
            
        }
    }
}
