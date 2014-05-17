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
	

	public static final String URL_BUKKIT = "http://dev.bukkit.org/server-mods/scs";
	
	
	/*
	 * Build information
	 */
	public static final double 	buildNumber;		// will be set by jenkins
	public static final String 	buildDate;			// will be set by jenkins
	
	public static final String 	BUILD_AUTHOR			= "kellerkindt";			// Author of the plugin
	public static final String  BUILD_CONTRIBUTOR	= "Ryzko, sorklin, rtainc";	// every very helpful person
	public static final boolean buildIsDev			= true;

        
    //Don't forget to update this number if any text changes in the locale files.
    public static final double  localeVersion       = 2.9;
    public static final int		VERSION_STORAGE		= 7;
    public static final int		VERSION_SHOP		= 1;
    
    

	
	static {
		
		double number 	= -1;
		String date		= "unknown";
		
		try {
			// loading jenkins information
			java.util.Properties properties = new java.util.Properties();
			properties.load(Properties.class.getResourceAsStream("/build.properties"));
			
			number 	= Double.parseDouble(properties.getProperty("buildNumber"));
			date 	= properties.getProperty("buildDate");
			
			
		} catch (Throwable t) {
			System.err.println ("Couldn't read build information!");
			System.err.println ("Although this build should work nomally, but without any build information!");
			t.printStackTrace();
			
		} finally {
			buildNumber = number;
			buildDate	= date;
		}
	}
    
    
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
//	
//	
//	/*
//	 * Filestore information
//	 */
//        //This isn't too smart, because NPE and IO errors can happen when new Files are called:
////	public static final File		dataPath 		= new File(ShowCaseStandalone.get().getDataFolder(), "data");
////	public static final File		dataBackupPath	= new File(ShowCaseStandalone.get().getDataFolder() + "/backup");
////	public static final File 		dataPathOld		= new File(ShowCaseStandalone.get().getDataFolder(), "cfg");
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
	public static final String		permMaxShopPerPlayerOverride= "scs.limitation.maxshops.override";
//       
//        
//        //Server customizable properties
//	public static int       		defaultUnit;
//	public static long      		maxUndoTime;
//	public static double    		buyShopCreatePrice;
//	public static double    		sellShopCreatePrice;
//	public static double    		displayCreatePrice;
//	public static double 			exchangeCreatePrice;
//	public static boolean   		fixBrokenShopsOnLoad;
//	public static String    		storageType;
//	public static String    		sqlUserame;
//	public static String    		sqlPass;
//	public static String			sqlURL;
//	public static String    		sqlDriver;
//	public static boolean   		blackList;
//	public static List<MaterialData>blockList;
//	public static boolean	   		buyBlackList;
//	public static List<MaterialData>buyList;
//	public static boolean	   		sellBlackList;
//	public static List<MaterialData>sellList;
//	public static List<String> 		blacklistedWorlds;
//	public static boolean   		cancelExplosion;
//	public static boolean   		logTransactions;
//	public static boolean   		requireObjectToDisplay;
//	public static boolean  			hideInactiveShops;
//	public static boolean			towny_needsResident;
//	public static boolean			towny_needsToBeOwner;
//	public static boolean			towny_allowInWilderness;
//	public static int				maxShopAmountPerPlayer;
//	public static boolean			startupDebugging	= false;
//	
//	public static EconomySystem		economySystem;
//	public static boolean	   		allowUnsafeEnchantments;
//	public static boolean 			hidden 				= false;
//	public static boolean 			threadDebug 		= false;
//	public static boolean	   		permDebug 			= false;
//	public static boolean	   		interactDebug 		= false;
//	public static boolean	   		showExtraMessages 	= false;
//	public static boolean	   		chunkDebug 			= false;
//	public static boolean	   		saveDebug 			= false;
//	public static boolean			maxAmountOnCreative	= true;
//	public static String	    	localizationFileName;
////	public static boolean	   		delayedSave;
////	public static int	       		delayedInterval;
//	public static boolean			stackToMaxAmount;	// currently deactivated
//	public static boolean			useSigns			= true;
//	
//	/**
//	 * Whether a display ShowCase can be used as a storage or not
//	 */
//	public static boolean			DISPLAY_USE_STORAGE	= true;
}
