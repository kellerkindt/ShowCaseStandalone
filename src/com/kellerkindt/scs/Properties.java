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
        AUTO                (new String[]{"com.iConomy.iConomy", "com.iCo6.iConomy", "com.iCo8.iConomy", "com.earth2me.essentials.Essentials", "cosine.boseconomy.BOSEconomy", "net.milkbowl.vault.Vault"}),
        iConomy             (new String[]{"com.iConomy.iConomy", "com.iCo6.iConomy", "com.iCo8.iConomy"}),
        EssentialsEconomy   (new String[]{"com.earth2me.essentials.Essentials"}),
        BOSEconomy          (new String[]{"cosine.boseconomy.BOSEconomy"}),
        Vault               (new String[]{"net.milkbowl.vault.Vault"}),
        ;
        public final String classNames[];
        private EconomySystem (String classNames[]) {
            this.classNames    = classNames;
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
    

    public static final String  URL_BUKKIT              = "http://dev.bukkit.org/server-mods/scs";
    
    public static final String  BUILD_AUTHOR            = "kellerkindt";    // Author of the plugin
    public static final String  BUILD_CONTRIBUTOR       = "sorklin, Ryzko"; // every very helpful person
    public static final boolean BUILD_ISDEV             = true;

        
    // don't forget to update these versions on locale, storage or shop changes
    public static final double  VERSION_LOCALE          = 3.0;
    public static final int     VERSION_STORAGE_SHOP    = 7;
    public static final int     VERSION_STORAGE_PRICE   = 1;
    public static final int     VERSION_SESSION         = 1;
    public static final int     VERSION_SHOP            = 3;
    public static final int     VERSION_NAMED_UUID      = 0;
      
    
    // default values
    public static final int     DEFAULT_PICKUP_DELAY    = Integer.MAX_VALUE;    // less pickup events
    public static final String[]DEFAULT_LOCALES         = {"locale_EN.yml", "locale_DE.yml"};
    
    public static final String  PATH_STORAGE                        = "yaml-storage";
    public static final String  PATH_SESSIONS                       = "sessions.yml";
    public static final String  PATH_PRICERANGE                     = "pricerange.yml";
        
    
    // permissions
    public static final String  PERMISSION_USE                      = "scs.use";
    public static final String  PERMISSION_CREATE_BUY               = "scs.create.buy";
    public static final String  PERMISSION_CREATE_SELL              = "scs.create.sell";
    public static final String  PERMISSION_CREATE_DISPLAY           = "scs.create.display";
    public static final String  PERMISSION_CREATE_DISPLAY_NOITEM    = "scs.create.display.reqnoitem";
    public static final String  PERMISSION_CREATE_EXCHANGE          = "scs.create.exchange";
    public static final String  PERMISSION_CREATE_UNLIMITED         = "scs.create.unlimited";
    public static final String  PERMISSION_REMOVE                   = "scs.remove";
    public static final String  PERMISSION_ADMIN                    = "scs.admin";
    public static final String  PERMISSION_MANAGE                   = "scs.manage";
    public static final String  PERMISSION_REPAIR                   = "scs.repair";
    
    // serialization
    public static final String ALIAS_NAMED_UUID                     = "scs.named-uuid";
    public static final String ALIAS_SHOP_BUY                       = "scs.buy";
    public static final String ALIAS_SHOP_SELL                      = "scs.sell";
    public static final String ALIAS_SHOP_DISPLAY                   = "scs.display";
    public static final String ALIAS_SHOP_EXCHANGE                  = "scs.exchange";
    public static final String ALIAS_PLAYERSESSION                  = "scs.playersession";
    public static final String ALIAS_PRICERANGE                     = "scs.pricerange";
    public static final String ALIAS_TRANSACTION                    = "scs.transaction";
    public static final String ALIAS_TRANSACTION_SHOPTYPE           = "scs.transaction.shoptype";
    
    // name of the key added to a player to mark it for a location-selection
    public static final String METADATA_PLAYER_LOCATIONSELECTOR     = "scs.locationselector";
    
}