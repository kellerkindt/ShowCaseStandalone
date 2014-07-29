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

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.DisplayShop;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Utilities;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Display extends Create {
    
    public Display(ShowCaseStandalone scs, String ... permissions) {
        super(scs, permissions, 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        
        // prepare
        Player        player        = (Player)sender;
        Shop        shop        = new DisplayShop(
                UUID.randomUUID(),
                player.getUniqueId(), 
                null,
                null
                );
        
        // parse the arguments
        shop.setItemStack    (Utilities    .getItemStack    (player,     args[0]));
        shop.setAmount        (0);
        shop.setPrice        (0);
        
        // unlimited?
        if (args.length > 1 && ARGUMENT_UNLIMITED.equals(args[1])) {
            shop.setUnlimited(true);
        }
        
        
        // register the shop, so its location will be set
        registerShopToCreate(player, shop);
        
    }

}
