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

import org.bukkit.entity.Player;

import com.kellerkindt.scs.exceptions.InsufficientPermissionException;

public interface InteractableShop {

    
    /**
     * Requests a set of the owner
     * @param isAdmin            if the request sender is an admin 
     * @param senderCanManage    if the request sender can manage a showcase
     * @param sender            the sender of this request
     * @param owner            the new owner
     * @return true if the request was successfully, false if not
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public boolean onSetOwner (boolean isAdmin, boolean senderCanManage, Player sender, String owner) throws InsufficientPermissionException;
    
    /**
     * Is called if the sender wants to get items from this shop
     * @param isAdmin            if the request sender is an admin
     * @param senderCanManage    if the request sender can manage a showcase
     * @param sender            the sender of this request
     * @param amount            the amount the sender wants to get
     * @return -1 if the request failed, otherwise the amount the sender got
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public int onGetItems (boolean isAdmin, boolean senderCanManage, Player sender, int amount) throws InsufficientPermissionException;
    
    /**
     * Is called if the sender wants to add items to this shop
     * @param isAdmin            if the request sender is an admin
     * @param senderCanManage    if the request sender can manage a showcase
     * @param sender            the sender of this request
     * @param amount            the amount the sender wants to get
     * @return -1 if the request failed, otherwise the amount the sender got
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public int onAddItems (boolean isAdmin, boolean senderCanManage, Player sender, int amount) throws InsufficientPermissionException;
    
    
    /**
     * Is called if the sender wants to set a new price for this shop
     * @param isAdmin            if the request sender is an admin
     * @param senderCanManage    if the request sender can manage a showcase
     * @param sender            the sender of this request
     * @param price                the new price for this showcase
     * @return true if the new price was set, false otherwise
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public boolean onSetPrice (boolean isAdmin, boolean senderCanManage, Player sender, double price) throws InsufficientPermissionException;
    
    /**
     * Is called if the sender wants to set a new (buy) limit
     * @param isAdmin            if the request sender is an admin
     * @param senderCanManage    if the request sender can manage a showcase
     * @param sender            the sender of this request
     * @param limit                the new (buy) limit
     * @return true if the new limit was set, false otherwise
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public boolean onSetLimit (boolean isAdmin, boolean senderCanManage, Player sender, int limit) throws InsufficientPermissionException;
    
    /**
     * Adds a member to the showcase
     * @param isAdmin            if the request sender is an admin
     * @param senderCanManage    if the request sender can manage a showcase
     * @param sender            the sender of this request
     * @param member            the member to add
     * @return true if the member was added successfully, false otherwise
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public boolean onAddMember (boolean isAdmin, boolean senderCanManage, Player sender, String member) throws InsufficientPermissionException;
    
    /**
     * Removes a member from the showcase
     * @param isAdmin            if the request sender is an admin
     * @param senderCanManage    if the request sender can manage a showcase
     * @param sender            the sender of this request
     * @param member            the member to remove
     * @return true if the member was removed successfully, false otherwise
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public boolean onRemMember (boolean isAdmin, boolean senderCanManage, Player sender, String member) throws InsufficientPermissionException;
    
    /**
     * Is called if the sender wants to get information about the showcase - left-clicked on this shop
     * @param isAdmin            if the request sender is an admin
     * @param senderCanUse        if the request sender can use a showcase
     * @param sender            the sender of this request
     * @return true if the information was send, false otherwise
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public boolean onSeeInfo (boolean isAdmin, boolean senderCanUse, Player sender) throws InsufficientPermissionException;
    
    /**
     * Is called if the sender wants to interact with this showcase - right-clicked on this shop
     * @param isAdmin            if the request sender is an admin
     * @param senderCanUse        if the request sender can use a showcase
     * @param sender            the sender of this request
     * @param amount            interact amount
     * @return true if the sender was able to interact, false otherwise
     * @throws InsufficientPermissionException    If the sender does not have the needed permissions
     */
    public boolean onInteract (boolean isAdmin, boolean senderCanUse, Player sender, int amount) throws InsufficientPermissionException;
    
    
}
