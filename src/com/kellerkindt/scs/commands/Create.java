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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kellerkindt.scs.LocationSelector;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseCreateEvent;
import com.kellerkindt.scs.shops.BuyShop;
import com.kellerkindt.scs.shops.Shop;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public abstract class Create extends SimpleCommand {
	
	public static final String			ARGUMENT_UNLIMITED	= "unlimited";
	public static final String			MATERIAL_CURRENT 	= "this";
	public static final List<String> 	TAB_MATERIAL_LIST;
	
	static {
		TAB_MATERIAL_LIST	= new ArrayList<String>();
		TAB_MATERIAL_LIST.add(MATERIAL_CURRENT);
		
		for (Material material : Material.values()) {
			TAB_MATERIAL_LIST.add(material.name());
		}
	}
	

	public Create(ShowCaseStandalone scs, String ... permissions) {
		this(scs, permissions, 3);
	}
	
	public Create(ShowCaseStandalone scs, String[] permissions, int minArgs) {
		super(scs, permissions, true, minArgs);
	}
	
	/**
	 * @param size Argument size to check
	 * @return Whether to add the material list to the auto completions for the given argument size
	 */
	protected boolean tabCompleteForArgumentSize (int size) {
		return size == 1;
	}
	
	/**
	 * Sets the amount on the shop (parsing the argument
	 * as Integer, or setting the shop to unlimited)
	 * 
	 * @param arg	Argument to parse
	 * @param shop	{@link Shop} to set the amount for
	 */
	protected void setAmount(Shop shop, String arg) {
		if (ARGUMENT_UNLIMITED.equalsIgnoreCase(arg)) {
			shop.setUnlimited(true);
		} else {
			int amount = Integer.parseInt(arg);
			
			if (shop instanceof BuyShop) {
				((BuyShop) shop).setMaxAmount(amount);
				
			} else {
				shop.setAmount(Integer.parseInt(arg));
			}
		}
	}
	
	@Override
	public List<String> getTabCompletions(CommandSender sender, String[] args) {
		
		List<String> list = null;
		
		// first argument is the material type
		if (tabCompleteForArgumentSize(args.length)) {
			list = new ArrayList<String>();
			
			for (String name : TAB_MATERIAL_LIST) {
				if (name.toLowerCase().startsWith(args[args.length-1].toLowerCase())) {
					list.add(name);
				}
			}
		}
		
		return list;
	}
	
	
	public void registerShopToCreate (final Player player, final Shop shop) {
		registerLocationSelector(player, new LocationSelector() {
			
			@Override
			public void onLocationSelected(Location location) {
				// update the location and perform the event
				shop.setLocation(location);
				scs.callShowCaseEvent(new ShowCaseCreateEvent(player, shop), player);
			}
		});
	}
}
