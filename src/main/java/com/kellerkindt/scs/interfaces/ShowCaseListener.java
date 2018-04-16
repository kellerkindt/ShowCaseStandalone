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

import org.bukkit.event.Listener;

import com.kellerkindt.scs.events.ShowCaseCreateEvent;
import com.kellerkindt.scs.events.ShowCaseDeleteEvent;
import com.kellerkindt.scs.events.ShowCaseInfoEvent;
import com.kellerkindt.scs.events.ShowCaseInteractEvent;
import com.kellerkindt.scs.events.ShowCaseItemAddEvent;
import com.kellerkindt.scs.events.ShowCaseItemRemoveEvent;
import com.kellerkindt.scs.events.ShowCaseLimitEvent;
import com.kellerkindt.scs.events.ShowCaseMemberAddEvent;
import com.kellerkindt.scs.events.ShowCaseMemberRemoveEvent;
import com.kellerkindt.scs.events.ShowCaseOwnerSetEvent;
import com.kellerkindt.scs.events.ShowCasePlayerBuyEvent;
import com.kellerkindt.scs.events.ShowCasePlayerExchangeEvent;
import com.kellerkindt.scs.events.ShowCasePlayerSellEvent;
import com.kellerkindt.scs.events.ShowCasePriceSetEvent;
import com.kellerkindt.scs.events.ShowCaseRemoveEvent;

/**
 * This interface will be used, to synchronize
 * the executive and verifying listener
 * @author kellerkindt <michael at kellerkindt.com>
 */
public interface ShowCaseListener extends Listener {

    /**
     * Is called, if a player requested information about a Shop
     * @param event    ShowCaseInfoEvent with needed information about the shop and player
     */
    public void onShowCaseInfoEvent (ShowCaseInfoEvent event);
    
    /**
     * Is called, if a player requested an interaction between him and the shop
     * @param event    ShowCaseInteractEvent with needed information about the shop and player
     */
    public void onShowCaseInteractEvent (ShowCaseInteractEvent event);
    
    /**
     * Is called, if items should be added to a shop
     * @param event    ShowCaseItemAddEvent with needed information about the shop and player
     */
    public void onShowCaseItemAddEvent (ShowCaseItemAddEvent event);
    
    /**
     * Is called, if a member should be added to a shop
     * @param event    ShowCaseMemberAddEvent with needed information about the shop and the member to add
     */
    public void onShowCaseMemberAddEvent (ShowCaseMemberAddEvent event);
    
    /**
     * Is called, if a new shop should be created
     * @param event    ShowCaseCreateEvent with needed information about the shop and player
     */
    public void onShowCaseCreateEvent (ShowCaseCreateEvent event);
    
    /**
     * Is called, if a shop should be deleted
     * @param event    ShowCaseDeleteEvent with needed information about the shop and player
     */
    public void onShowCaseDeleteEvent (ShowCaseDeleteEvent event);    
    
    /**
     * Is called, if items should be removed from a shop
     * @param event    ShowCaseItemRemoveEvent with needed information about the shop and player
     */
    public void onShowCaseItemRemoveEvent (ShowCaseItemRemoveEvent event);
    
    
    /**
     * Is called, if a new limit should set for the shop
     * @param event ShowCaseLimitEvent with needed information about the shop and player
     */
    public void onShowCaseLimitEvent (ShowCaseLimitEvent event);
    
    /**
     * Is called, if the shop should be removed
     * @param event    ShowCaseRemoveEvent with needed information about the shop and player
     */
    public void onShowCaseRemoveEvent (ShowCaseRemoveEvent event);
    
    /**
     * Is called, if a member should be removed
     * @param event    ShowCaseMemeberRemoveEvent with needed information about the shop and player
     */
    public void onShowCaseMemberRemoveEvent (ShowCaseMemberRemoveEvent event);
    
    /**
     * Is called, if the owner of a shop should be set to another one
     * @param event    ShowCaseOwnerSetEvent with needed information about the shop and player
     */
    public void onShowCaseOwnerSetEvent (ShowCaseOwnerSetEvent event);
    
    /**
     * Is called, if the price of a shop should be set to another one
     * @param event ShowCasePriceSetEvent with needed information about the shop and player
     */
    public void onShowCasePriceSetEvent (ShowCasePriceSetEvent event);
    
    /**
     * Is called, if the player wants to buy something from a shop
     * @param event ShowCasePlayerBuyEvent with needed information about the shop and player
     */
    public void onShowCasePlayerBuyEvent (ShowCasePlayerBuyEvent event);
    
    /**
     * Is called, if the player wants to sell something to a shop
     * @param event ShowCasePlayerSellEvent with needed information about the shop and player
     */
    public void onShowCasePlayerSellEvent (ShowCasePlayerSellEvent event);
    
    /**
     * Is called, if the player wants to exchange items from a shop
     * @param event ShowCasePlayerExchangeEvent with needed information about the shop and player
     */
    public void onShowCasePlayerExchangeEvent (ShowCasePlayerExchangeEvent event);
}
