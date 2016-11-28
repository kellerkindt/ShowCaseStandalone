/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-11-28 22:51 +01 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
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

import com.kellerkindt.scs.ShopManipulator;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.Shop;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * @author Michael Watzko <michael at kellerkindt.com>
 */
public class HoverText extends SimpleCommand {

    public HoverText(ShowCaseStandalone scs, String...permissions) {
        super(scs, permissions, true, 0);
    }

    @Override
    public java.util.List<String> getTabCompletions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        String text = null;

        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(args[0]);
            for (int i = 1; i < args.length; ++i) {
                builder.append(' ');
                builder.append(args[i]);
            }
            text = builder.toString();
        }

        final String hoverText = text;

        registerShopManipulator(
                (Player) sender,
                new ShopManipulator() {
                    @Override
                    public void manipulate(Shop shop) {
                        shop.setCustomHoverText(hoverText);
                        scs.getShopHandler().hide(shop);
                        scs.getShopHandler().show(shop);
                    }

                    @Override
                    public boolean requiresValidShop() {
                        return true;
                    }
                }
        );
    }
}
