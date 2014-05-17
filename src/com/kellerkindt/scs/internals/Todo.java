/**
* ShowCaseStandalone
* Copyright (C) 2012 Kellerkindt <copyright at kellerkindt.com>
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
package com.kellerkindt.scs.internals;

import org.bukkit.entity.Player;

import com.kellerkindt.scs.shops.Shop;

@Deprecated
public class Todo {
	public enum Type {
		CREATE,
		REMOVE,
		ADD_ITEMS,
		GET_ITEMS,
		LIMIT,
		SET_PRICE,
		SET_OWNER,
		ADD_MEMBER,
		REMOVE_MEMBER,
		DESTROY,
	}
	public final Player Player;
	public final Type   Type;
	public final Shop   Shop;
	public final double Amount;
	public final String String;
        
	public Todo (Player player, Type type, Shop shop, double amount, String string) {
		this.Player	= player;
		this.Type	= type;
		this.Shop	= shop;
		this.Amount	= amount;
		this.String = string;
	}
}
