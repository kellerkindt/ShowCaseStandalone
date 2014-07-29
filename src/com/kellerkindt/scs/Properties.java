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
package com.kellerkindt.scs;





public class Properties {
	public enum EconomySystem {
		AUTO				(new String[]{"com.iConomy.iConomy", "com.iCo6.iConomy", "com.iCo8.iConomy", "com.earth2me.essentials.Essentials", "cosine.boseconomy.BOSEconomy", "net.milkbowl.vault.Vault"}),
		iConomy				(new String[]{"com.iConomy.iConomy", "com.iCo6.iConomy", "com.iCo8.iConomy"}),
		EssentialsEconomy	(new String[]{"com.earth2me.essentials.Essentials"}),
		BOSEconomy			(new String[]{"cosine.boseconomy.BOSEconomy"}),
		Vault				(new String[]{"net.milkbowl.vault.Vault"}),
		;
		public final String classNames[];
		private EconomySystem (String classNames[]) {
			this.classNames	= classNames;
		}
		
		/**
		 * @param name Name of the {@link EconomySystem} to get
		 * @return The {@link EconomySystem} for the given name or null
		 */
		public static EconomySystem getForName (String name) {
			for (EconomySystem system : values()) {
				if (system.toString().equalsIgnoreCase(name)) {
					return system;
				}
			}
			
			return null;
		}
	}
	

	public static final String URL_BUKKIT 			= "http://dev.bukkit.org/server-mods/scs";
	
	public static final String 	BUILD_AUTHOR		= "kellerkindt";			// Author of the plugin
	public static final String  BUILD_CONTRIBUTOR	= "sorklin, Ryzko";			// every very helpful person
	public static final boolean BUILD_ISDEV			= true;

        
    //Don't forget to update this number if any text changes in the locale files.
    public static final double  VERSION_LOCALE		= 2.9;
    public static final int		VERSION_STORAGE		= 7;
    public static final int		VERSION_SHOP		= 1;
      
//
//	/*
//	 * For SimpleShopHandler (Thread which checks if the items are dead)
//	 * Note: i'm slowing this down as much as possible, to conserve server CPU.  60 is default
//         * but is configurable in the config.yml
//	 */
//	public static long   intervall = 60;
//	
//	
	// shop values
	public static final boolean	DEFAULT_STACK_TO_MAX	= false;
	public static final int		DEFAULT_STACK_AMOUNT	= 0;					// 0 makes it not pickupable!! o.O
	public static final int		DEFAULT_PICKUP_DELAY	= Integer.MAX_VALUE;	// less pickup events
	
	// TODO
	public static final String  	dataEncoding	= "UTF-8";
	
	public static final String		commentSign			= "comment";
	public static final String  	seperator			= ";";
	public static final String[]	defaultLocaleFiles 	= {"locale_EN.yml", "locale_DE.yml"};
        
        //Permissions.  I made these more granular, in case people want to limit users to certain types.
	// TODO rename
	public static final String 		PERMISSION_USE 					= "scs.use";
	public static final String		PERMISSION_CREATE_BUY 				= "scs.create.buy";
	public static final String 		PERMISSION_CREATE_SELL 				= "scs.create.sell";
	public static final String 		PERMISSION_CREATE_DISPLAY 			= "scs.create.display";
	public static final String		permCreateDisplayNoItem		= "scs.create.display.reqnoitem";
	public static final String 		PERMISSION_CREATE_EXCHANGE 			= "scs.create.exchange";
	public static final String 		permCreateUnlimited 		= "scs.create.unlimited";
	public static final String 		PERMISSION_REMOVE 					= "scs.remove";
	public static final String 		PERMISSION_ADMIN 					= "scs.admin";
	public static final String 		PERMISSION_MANAGE 					= "scs.manage";
	public static final String 		PERMISSION_REPAIR					= "scs.repair";
}
