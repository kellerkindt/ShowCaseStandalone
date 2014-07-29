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

import com.kellerkindt.scs.EventShopManipulator;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseEvent;
import com.kellerkindt.scs.events.ShowCaseInfoEvent;
import com.kellerkindt.scs.events.ShowCaseMemberAddEvent;
import com.kellerkindt.scs.events.ShowCaseMemberRemoveEvent;
import com.kellerkindt.scs.exceptions.MissingOrIncorrectArgumentException;
import com.kellerkindt.scs.shops.Shop;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Member extends SimpleCommand {
    
    public static final String MEMBER_LIST        = "list";
    public static final String MEMBER_ADD        = "add";
    public static final String MEMBER_REMOVE    = "remove";
    
    public static final List<String> LIST_TAB    = Arrays.asList(MEMBER_LIST, MEMBER_ADD, MEMBER_REMOVE);

    public Member(ShowCaseStandalone scs, String ...permissions) {
        super(scs, permissions, true, 2);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> list     = new ArrayList<String>();
        String         current= args.length > 0 ? args[0] : "";
        
        for (String cmd : LIST_TAB) {
            if (cmd.toLowerCase().startsWith(current.toLowerCase())) {
                list.add(cmd);
            }
        }
        
        return list;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        
        final Player    player    = (Player)sender;
        final String    cmd        = args[0];
        final String    member    = args.length > 1 ? args[1] : "";
        
        // verify the command
        if (!MEMBER_LIST.equalsIgnoreCase(cmd) && !MEMBER_ADD.equalsIgnoreCase(cmd) && !MEMBER_REMOVE.equalsIgnoreCase(cmd)) {
            throw new MissingOrIncorrectArgumentException();
        }
        
        registerShopManipulator(player, new EventShopManipulator(scs, sender){
            @Override
            public ShowCaseEvent getEvent(Shop shop) {
                if (MEMBER_LIST.equalsIgnoreCase(cmd)) {
                    // TODO
                    return new ShowCaseInfoEvent(player, shop);
                }
                
                else if (MEMBER_ADD.equalsIgnoreCase(cmd)) {
                    return new ShowCaseMemberAddEvent(player, shop, member);
                }
                
                else if (MEMBER_REMOVE.equalsIgnoreCase(cmd)) {
                    return new ShowCaseMemberRemoveEvent(player, shop, member);
                }
                
                // should never happen since there is a check before
                return null;
            }
        });
    }
    
}
