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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public interface Command {

    /**
     * @param sender    {@link CommandSender} to test for
     * @return Whether the given {@link CommandSender} can execute this command
     */
    public boolean hasPermissions(CommandSender sender);
    
    /**
     * @return The name of this command
     */
    public String getName ();
    
    /**
     * @return The amount of arguments that are required to execute this command
     */
    public int getMinArgumentCount ();
    
    /**
     * @return Whether the {@link CommandSender} has to be a {@link Player}
     */
    public boolean hasToBeAPlayer();
    
    /**
     * @param sender    The {@link CommandSender} that requested tab complete
     * @param args        Arguments at the moment tab complete has been requested
     * @return A {@link List} of possible completions for the last argument or null
     */
    public List<String> getTabCompletions (CommandSender sender, String[] args);
    
    /**
     * @param sender        {@link CommandSender} that requested the execution
     * @param args            Additional arguments
     * @throws CommandException    Only if it wasn't possible to execute the command
     */
    public void execute (CommandSender sender, String[] args) throws CommandException;
}
