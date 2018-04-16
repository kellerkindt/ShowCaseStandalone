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

package com.kellerkindt.scs.interfaces;

import com.kellerkindt.scs.commands.CommandException;
import org.bukkit.command.CommandSender;

/**
 * @author Michael <michael at kellerkindt.com>
 */
public interface MultiStageCommand extends RunLater {

    /**
     * @see {@link com.kellerkindt.scs.commands.Command#execute(CommandSender, String[])}
     */
    void execute(CommandSender sender, String args[]) throws CommandException;
}
