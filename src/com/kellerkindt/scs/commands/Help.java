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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.exceptions.MissingOrIncorrectArgumentException;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Help extends SimpleCommand {

    private Map<String, Entry<String, List<String>>> sites = new HashMap<String, Entry<String, List<String>>>();
    
    public Help(ShowCaseStandalone scs, String ... permissions) {
        super(scs, permissions);
        
        sites.put("admin", new AbstractMap.SimpleEntry<String, List<String>>(permissions[1], Arrays.asList(
                Term.HELP_TITLE_ADMIN.get(),
                Term.HELP_ADMIN_1.get(),
                Term.HELP_ADMIN_2.get(),
                Term.HELP_ADMIN_3.get(),
                Term.HELP_ADMIN_4.get(),
                Term.HELP_ADMIN_5.get(),
                Term.HELP_ADMIN_6.get(),
                Term.HELP_ADMIN_7.get(),
                Term.HELP_ADMIN_8.get(),
                Term.HELP_ADMIN_9.get(),
                Term.HELP_ADMIN_10.get()
                )));
        
        sites.put("1", new AbstractMap.SimpleEntry<String, List<String>>(permissions[0], Arrays.asList(
                Term.HELP_TITLE.get("1"),
                Term.HELP_1.get(),
                Term.HELP_2.get(),
                Term.HELP_3.get(),
                Term.HELP_4.get(),
                Term.HELP_5.get(),
                Term.HELP_6.get(),
                Term.HELP_7.get(),
                Term.HELP_25.get(),
                Term.HELP_26.get(),
                Term.HELP_8.get(),
                Term.HELP_9.get(),
                Term.HELP_10.get(),
                Term.HELP_27.get(),
                Term.HELP_14.get(),
                Term.HELP_15.get(),
                Term.HELP_24.get()
                )));
        
        sites.put("2", new AbstractMap.SimpleEntry<String, List<String>>(permissions[0], Arrays.asList(
                Term.HELP_TITLE.get("2"),
                Term.HELP_16.get(),
                Term.HELP_12.get(),
                Term.HELP_13.get(),
                Term.HELP_17.get(),
                Term.HELP_18.get(),
                Term.HELP_19.get(),
                Term.HELP_20.get(),
                Term.HELP_21.get(),
                Term.HELP_22.get(),
                Term.HELP_28.get(),
                Term.HELP_29.get(),
                Term.HELP_30.get(),
                Term.HELP_31.get()
                )));
        
    }

    @Override
    public boolean hasPermissions(CommandSender sender) {
        return scs.hasOnePermission(sender, permissions);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // get the requested page
        String             page     = args.length > 0 ? args[0] : "";
        List<String>    list    = new ArrayList<String>();
        
        for (Entry<String, Entry<String, List<String>>> entry : sites.entrySet()) {
            if (entry.getKey().startsWith(page) && scs.hasPermission(sender, entry.getValue().getKey())) {
                list.add(entry.getKey());
            }
        }
        
        // TODO
        if ("1".equals(page) && scs.isAdmin(sender)) {
            list.add(Term.HELP_23.get());
            
        }
        
        return list;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        
        // get the selected page
        String                         page     = args.length > 0 ? args[0] : "1";
        Entry<String, List<String>>    entry    = sites.get(page);
        
        if (entry == null) {
            throw new MissingOrIncorrectArgumentException();
        }
        
        if (!scs.hasPermission(sender, entry.getKey())) {
            // if the sender hasn't the permissions, do as if it does not exist
            throw new MissingOrIncorrectArgumentException();
        }
        
        for (String line : entry.getValue()) {
            scs.sendMessage(sender, line);
        }
    }

}
