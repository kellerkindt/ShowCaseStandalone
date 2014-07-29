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
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.PlayerSession;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Message extends SimpleCommand {
    
    public static final String             MESSAGE_RECEIVE    = "receive";
    public static final String             MESSAGE_IGNORE    = "ignore";
    public static final List<String>    LIST_TAB        = Arrays.asList(MESSAGE_RECEIVE, MESSAGE_IGNORE);

    public Message(ShowCaseStandalone scs, String...permissions) {
        super(scs, permissions, true);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String>     list     = new ArrayList<String>();
        String             current    = args.length > 0 ? args[0] : "";
        
        for (String string : LIST_TAB) {
            if (string.toLowerCase().startsWith(current.toLowerCase())) {
                list.add(string);
            }
        }
        
        return list;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        
         // get the players session
        PlayerSession session = scs.getPlayerSessionHandler().getSession((Player)sender);
        
        //This is the catch all message (meaning true or false isn't specified):
        String msg = session.showTransactionMessage() 
                    ? Term.IGNORE_RECEIVE.get("receiving")
                    : Term.IGNORE_RECEIVE.get("ignoring");
        
        // set?
        if(args.length > 0)
            if(args[0].equalsIgnoreCase(MESSAGE_IGNORE)){
                session.setShowTransactionMessage(false);
                msg = Term.IGNORE_TRANSACTION.get("ignored");
                
            } else if (args[0].equalsIgnoreCase(MESSAGE_RECEIVE)) {
                session.setShowTransactionMessage(true);
                msg = Term.IGNORE_TRANSACTION.get("received");
            }
        
        
        scs.sendMessage(sender, msg);
    }
    
    
}
