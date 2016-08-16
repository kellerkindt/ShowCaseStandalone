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

import com.kellerkindt.scs.interfaces.MultiStageCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.LocationSelector;
import com.kellerkindt.scs.ShopManipulator;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public abstract class SimpleCommand implements Command {
    
    protected     ShowCaseStandalone    scs;
    protected     String[]             permissions;
    protected     String                name;
    protected    int                    minArguments;
    protected    boolean                mustBePlayer;
    
    public SimpleCommand(ShowCaseStandalone scs) {
        this(scs, new String[0]);
    }
    
    public SimpleCommand(ShowCaseStandalone scs, String[] permissions) {
        this(scs, permissions, false);
    }
    
    public SimpleCommand(ShowCaseStandalone scs, String[] permissions, boolean player) {
        this(scs, permissions, player, 0);
    }
    
    public SimpleCommand(ShowCaseStandalone scs, String[] permissions, boolean player, int min) {
        this(scs, permissions, player, min, null);
    }
    
    public SimpleCommand(ShowCaseStandalone scs, String[] permissions, boolean player, int min, String name) {
        this.scs            = scs;
        this.permissions    = permissions;
        this.mustBePlayer    = player;
        this.minArguments    = min;
        this.name            = name;
        
        checkName();
    }
    
    /**
     * Sets the name for this command to this
     * simple class name if not set
     */
    private void checkName () {
        if (name == null) {
            name = getClass().getSimpleName().toLowerCase();
        }
    }
    
    /**
     * Registers the given {@link ShopManipulator}, if not executed
     * immediately, it also demands the {@link Player} to select a {@link Shop}
     * 
     * @param player        {@link Player} to register the {@link ShopManipulator} on
     * @param manipulator    {@link ShopManipulator} to execute
     */
    public void registerShopManipulator (Player player, ShopManipulator manipulator) {
        if (!scs.registerShopManipulator(player, manipulator)) {
            scs.sendMessage(player, Term.NEXT.get());
        }
    }
    
    /**
     * Registers the given {@link LocationSelector} for the given player,
     * will also tell the given {@link Player} to select a location
     * 
     * @param player    {@link Player} to register the {@link LocationSelector} on
     * @param selector    {@link LocationSelector} to register for the given {@link Player}
     */
    public void registerLocationSelector (Player player, LocationSelector selector) {
        scs.registerLocationSelector(player, selector);
        scs.sendMessage(player, Term.NEXT.get());
    }


    /**
     * @param player {@link Player} to register the given {@link MultiStageCommand} for
     * @param command {@link MultiStageCommand} to register
     * @param message Message, containing the instructions for what to do, to send to the {@link Player}
     */
    public void registerMultiStageCommand(Player player, MultiStageCommand command, String message) {
        scs.registerRunLater(player, command);
        scs.sendMessage(player, message);
    }

    @Override
    public boolean hasPermissions(CommandSender sender) {
        return scs.hasAllPermissions(sender, permissions);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMinArgumentCount() {
        return minArguments;
    }
    
    @Override
    public boolean hasToBeAPlayer() {
        return mustBePlayer;
    }
}
