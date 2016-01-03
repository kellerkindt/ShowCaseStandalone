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

import com.kellerkindt.scs.internals.NamedUUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.EventShopManipulator;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseEvent;
import com.kellerkindt.scs.events.ShowCaseOwnerSetEvent;
import com.kellerkindt.scs.shops.Shop;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Owner extends SimpleCommand {

    public Owner (ShowCaseStandalone scs, String...permissions) {
        super(scs, permissions, true, 0);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // ignore
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        
        final Player player = (Player)sender;
        final String name   = (args != null && args.length > 0) ? args[0] : null;
        
        registerShopManipulator(player, new EventShopManipulator(scs, sender) {
            @Override
            public ShowCaseEvent getEvent(Shop shop) {
                return new ShowCaseOwnerSetEvent(player, shop, new NamedUUID(
                        name != null ? scs.getPlayerUUID(name) : null,
                        name
                ));
            }
        });
    }
    
    
    
}
