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
package com.kellerkindt.scs.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kellerkindt.scs.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.Properties;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.exceptions.MissingOrIncorrectArgumentException;
import com.kellerkindt.scs.utilities.Term;

public class CommandExecutorListener implements CommandExecutor, TabCompleter {
    
    private ShowCaseStandalone     scs;
    
    private List<com.kellerkindt.scs.commands.Command> commands = new ArrayList<>();
    
    public CommandExecutorListener (ShowCaseStandalone scs) {
        this.scs = scs;
        
        // TODO
        commands.add(new Abort                              (scs, Properties.PERMISSION_USE));
        commands.add(new About                              (scs, Properties.PERMISSION_USE));
        commands.add(new Add                                (scs, Properties.PERMISSION_USE));
        commands.add(new Amount                             (scs, Properties.PERMISSION_MANAGE));
        commands.add(new Buy                                (scs, Properties.PERMISSION_CREATE_BUY));
        commands.add(new Clear                              (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Destroy                            (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Disable                            (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Display                            (scs, Properties.PERMISSION_CREATE_DISPLAY));
        commands.add(new Exchange                           (scs, Properties.PERMISSION_CREATE_EXCHANGE));
        commands.add(new That                               (scs, Properties.PERMISSION_CREATE_EXCHANGE));
        commands.add(new Get                                (scs, Properties.PERMISSION_MANAGE));
        commands.add(new Help                               (scs, Properties.PERMISSION_USE, Properties.PERMISSION_ADMIN));
        commands.add(new HoverText                          (scs, Properties.PERMISSION_MANAGE));
        commands.add(new Last                               (scs, Properties.PERMISSION_USE));
        commands.add(new com.kellerkindt.scs.commands.List  (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Member                             (scs, Properties.PERMISSION_MANAGE));
        commands.add(new Message                            (scs, Properties.PERMISSION_USE));
        commands.add(new Owner                              (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Price                              (scs, Properties.PERMISSION_MANAGE));
        commands.add(new Prune                              (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Purge                              (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Range                              (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Reload                             (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Remove                             (scs, Properties.PERMISSION_REMOVE));
        commands.add(new Repair                             (scs, Properties.PERMISSION_REPAIR));
        commands.add(new Report                             (scs, Properties.PERMISSION_ADMIN));
        commands.add(new Sell                               (scs, Properties.PERMISSION_CREATE_SELL));
        commands.add(new Undo                               (scs, Properties.PERMISSION_USE));
        commands.add(new Unit                               (scs, Properties.PERMISSION_USE));
        commands.add(new Version                            (scs, Properties.PERMISSION_USE));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String lable, String[] args) {
        
        List<String> list = null;
        
        // hasn't found a sub-command yet
        if (args.length < 1) {
            list = new ArrayList<>();
            
            for (com.kellerkindt.scs.commands.Command cmd : commands) {
                list.add(cmd.getName());
            }
            
            
        } else if (args.length == 1) {
            list = new ArrayList<>();
            
            for (com.kellerkindt.scs.commands.Command cmd : commands) {
                if (cmd.getName().startsWith(args[0]) && cmd.hasPermissions(sender)) {
                    list.add(cmd.getName());
                }
            }
            
            
        } else if (args.length > 1) {
            com.kellerkindt.scs.commands.Command cmd = null;
            
            // check whether there is a suitable command
            for (com.kellerkindt.scs.commands.Command c : commands) {
                if (c.getName().equals(args[0]) && c.hasPermissions(sender)) {
                    cmd = c;
                    break;
                }
            }
            
            // get the commands tab complete if found
            if (cmd != null) {
                list = cmd.getTabCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        
        return list;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        
        try {
            com.kellerkindt.scs.commands.Command cmd = null;
            
            // try to find the command
            if (args != null && args.length > 0) {
                for (com.kellerkindt.scs.commands.Command c : commands) {
                    if (c.getName().equals(args[0])) {
                        cmd = c;
                        break;
                    }
                }
            }
            
            if (cmd == null) {
                // let the sender know
                scs.sendMessage(sender, Term.ERROR_COMMAND_UNKNOWN.get());
                return true;
            }
            
            if (!cmd.hasPermissions(sender)) {
                // let the sender know
                scs.sendMessage(sender, Term.ERROR_INSUFFICIENT_PERMISSION.get());
                return true;
            }
            
            if (cmd.hasToBeAPlayer() && !(sender instanceof Player)) {
                // let the sender know
                scs.sendMessage(sender, Term.ERROR_EXECUTE_AS_PLAYER.get());
                return true;
            }
            
            if (cmd.getMinArgumentCount() > (args.length-1)) {
                // let the sender know
                scs.sendMessage(sender, Term.ERROR_MISSING_OR_INCORRECT_ARGUMENT.get());
                return true;
            }
            
            
            // execute it
            cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            
        } catch (Throwable t) {
            
            // only print the exception if somehow expected
            if (!(t instanceof MissingOrIncorrectArgumentException)) {
                t.printStackTrace();
                scs.sendMessage(sender, Term.ERROR_MISSING_OR_INCORRECT_ARGUMENT.get());
            }
            
            else {
                scs.getLogger().warning("Invalid command call by "+sender.getName()+": "+command);
                scs.sendMessage(sender, t.getMessage());
            }
            
        }
        
        return true;
    }
}