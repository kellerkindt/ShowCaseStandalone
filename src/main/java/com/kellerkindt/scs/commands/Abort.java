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

import java.util.List;

import com.kellerkindt.scs.interfaces.RunLater;
import org.bukkit.command.CommandSender;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.utilities.Term;
import org.bukkit.entity.Player;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Abort extends SimpleCommand {

    public Abort(ShowCaseStandalone scs, String ... permissions) {
        super(scs, permissions, true);
    }


    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // nothing to do at all
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            RunLater runLater = scs.removeRunLater((Player)sender);

            if (runLater != null) {
                runLater.abort((Player)sender);
            }

            scs.sendMessage(sender, Term.ABORT.get());
        }
    }

    
    
}
