/**
* ShowCaseStandalone
* Copyright (C) 2013 Kellerkindt <copyright at kellerkindt.com>
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class SCSConfiguration extends Configuration {
    
    public static final String KEY_DEFAULTUNIT              = "Default.Unit";
    public static final String KEY_DEFAULTSHOWTRANSMESSG    = "Default.ShowTransactionMessages";
    public static final String KEY_DEFAULTSHOWTRANSMESSGUN  = "Default.ShowTransactionMessagesOnUnlimited";
    
    public static final String KEY_ECONOMYSYSTEM            = "EconomySystem";
    public static final String KEY_ALLOWUNSAFEENCHANTMENTS  = "AllowUnsafeEnchantments";
    public static final String KEY_CANCELEXPLOSIONS         = "CancelExplosions";
    public static final String KEY_HIDEINACTIVESHOPS        = "HideInactiveShops";
    public static final String KEY_MAXAMOUNTONCREATIVE      = "MaxAmountOnCreative";
    public static final String KEY_REQUIREOBJECTTODISPLAY   = "RequireObjectToDisplay";
    
    public static final String KEY_CREATEPRICE_BUYSHOP      = "CreatePrice.BuyShop";
    public static final String KEY_CREATEPRICE_SELLSHOP     = "CreatePrice.SellShop";
    public static final String KEY_CREATEPRICE_DISPLAY      = "CreatePrice.Display";
    public static final String KEY_CREATEPRICE_EXCHANGE     = "CreatePrice.Exchange";
    
    public static final String KEY_SAVE_INTERVAL            = "Save.Interval";
    
    public static final String KEY_DISPLAY_USESIGNS         = "Display.UseSigns";
    public static final String KEY_DISPLAY_USESTORAGE       = "Display.UseStorage";
    
    public static final String KEY_TOWNY_NEEDSRESIDENT      = "Towny.needsResident";
    public static final String KEY_TOWNY_NEEDSTOBEOWNER     = "Towny.needsToBeOwner";
    public static final String KEY_TOWNY_ALLOWINWILDERNESS  = "Towny.allowInWilderness";
    
    // debug flags, currently not in use :(
    public static final String KEY_DEBUG_THREAD             = "Debug.Thread";
    public static final String KEY_DEBUG_INTERACT           = "Debug.Interact";
    public static final String KEY_DEBUG_PERMISSIONS        = "Debug.Permissions";
    public static final String KEY_DEBUG_CHUNK              = "Debug.Chunk";
    public static final String KEY_DEBUG_SAVE               = "Debug.Save";
    public static final String KEY_DEBUG_SHOWEXTRAMESSAGES  = "Debug.ShowExtraMessages";
    public static final String KEY_DEBUG_LOG                = "Debug.Log";
    
    public static final String KEY_LOCALIZATION_FILE        = "Localization.File";
    public static final String KEY_LOCALIZATION_VERSION     = "Localization.Version";
    
    public static final String KEY_LIMITATION_MAXAMOUNT     = "Limitation.MaxAmount";
    
    public static final String KEY_BLOCKLIST_BLACKLIST      = "BlockList.BlackList";
    public static final String KEY_BLOCKLIST_BLOCKS         = "BlockList.Blocks";
    
    public static final String KEY_SELLITEMLIST_BLACKLIST   = "SellItemList.BlackList";
    public static final String KEY_SELLITEMLIST_ITEMS       = "SellItemList.Items";
    
    public static final String KEY_BUYITEMLIST_BLACKLIST    = "BuyItemList.BlackList";
    public static final String KEY_BUYITEMLIST_ITEMS        = "BuyItemList.Items";
    
    public static final String KEY_WORLDBLACKLIST           = "WorldBlacklist";
    
    public static final String KEY_RESIDENCE_HOOK           = "Residence.hook";
    public static final String KEY_RESIDENCE_FLAG           = "Residence.flag";
    public static final String KEY_RESIDENCE_ALLOWOWNER     = "Residence.allowOwner";
    
    
    public SCSConfiguration(FileConfiguration config) {
        super(config);
        
        // "import" old values / apply updates
        rename("DefaultUnit",                        "Default.Unit");                     // 2014-03-02
        rename("DefaultShowTransactionMessages",    "Default.ShowTransactionMessages"); // 2014-03-02
        
        
        // update / set the configuration with default values
        for (Method method : getClass().getDeclaredMethods()) {
            // is a getter?
            if (Modifier.isPublic(method.getModifiers())
                    && !(method.getReturnType().equals(Void.TYPE))
                    && method.getParameterTypes().length == 0
                    && method.getExceptionTypes().length == 0) {
                try {
                    // invoke it, so it is added to the configuration
                    method.invoke(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean getResidenceHookInto () {
        return getForced(KEY_RESIDENCE_HOOK, true);
    }
    
    public String getResidenceFlag () {
        return getForced(KEY_RESIDENCE_FLAG, "shops"); // TODO
    }
    
    public boolean getResidenceAllowOwner () {
        return getForced(KEY_RESIDENCE_ALLOWOWNER, true);
    }

    /**
     * @return The default unit for an interaction with shift
     */
    public int getDefaultUnit () {
        return getForced(KEY_DEFAULTUNIT, 64);
    }
    
    public boolean getDefaultShowTransactionMessage () {
        return getForced(KEY_DEFAULTSHOWTRANSMESSG, true);
    }
    
    public boolean getDefaultShowTransactionMessageOnUnlimited () {
        return getForced(KEY_DEFAULTSHOWTRANSMESSGUN, false);
    }
    
    public String getEconomySystem () {
        return getForced(KEY_ECONOMYSYSTEM, "Vault");
    }
    
    public boolean isCancelingExplosions () {
        return getForced(KEY_CANCELEXPLOSIONS, false);
    }
    
    public boolean isHidingInactiveShops () {
        return getForced(KEY_HIDEINACTIVESHOPS, false);
    }
    
    public boolean isMaxAmountOnCreative () {
        return getForced(KEY_MAXAMOUNTONCREATIVE, true);
    }
    
    public double getCreatePriceBuyShop () {
        return getForced(KEY_CREATEPRICE_BUYSHOP, 0.0);
    }
    
    public double getCreatePriceSellShop () {
        return getForced(KEY_CREATEPRICE_SELLSHOP, 0.0);
    }
    
    public double getCreatePriceDisplay () {
        return getForced(KEY_CREATEPRICE_DISPLAY, 0.0);
    }
    
    public double getCreatePriceExchange () {
        return getForced(KEY_CREATEPRICE_EXCHANGE, 0.0);
    }
    
    public long getSaveInterval () {
        return getForced(KEY_SAVE_INTERVAL, 60l);
    }
    
    public boolean isDebuggingThreads () {
        return getForced(KEY_DEBUG_THREAD, false);
    }
    
    public boolean isDebuggingPermissions () {
        return getForced(KEY_DEBUG_PERMISSIONS, false);
    }
    
    public boolean isDebuggingChunks () {
        return getForced(KEY_DEBUG_CHUNK, false);
    }
    
    public boolean isDebuggingLog () {
        return getForced(KEY_DEBUG_LOG, false);
    }
    
    public boolean isDisplayShopUsingSigns () {
        return getForced(KEY_DISPLAY_USESIGNS, true);
    }
    
    public boolean isDisplayShopUsingStorage () {
        return getForced(KEY_DISPLAY_USESTORAGE, true);
    }
    
    public boolean isTownyAllowingInWilderness () {
        return getForced(KEY_TOWNY_ALLOWINWILDERNESS, false);
    }
    
    public boolean isTownyNeedingResident () {
        return getForced(KEY_TOWNY_NEEDSRESIDENT, true);
    }
    
    public boolean isTownyNeedingToBeOwner () {
        return getForced(KEY_TOWNY_NEEDSTOBEOWNER, false);
    }
    
    public String getLocalizationFile () {
        return getForced(KEY_LOCALIZATION_FILE, "locale_EN.yml");
    }
    
    public double getLocalizationVersion () {
        return getForced(KEY_LOCALIZATION_VERSION, -1.0);
    }
    
    public void setLocalizationVersion (double version) {
        update(KEY_LOCALIZATION_VERSION, version);
    }
    
    public int getLimitationMaxAmountPerPlayer () {
        return getForced(KEY_LIMITATION_MAXAMOUNT, -1);
    }
    
    public boolean isBlockListBlacklist () {
        return getForced(KEY_BLOCKLIST_BLACKLIST, true);
    }
    
    public List<String> getBlockListBlocks () {
        return getForced(KEY_BLOCKLIST_BLOCKS, new ArrayList<String>());
    }
    
    public boolean isSellItemListBlacklist () {
        return getForced(KEY_SELLITEMLIST_BLACKLIST, true);
    }
    
    public List<String> getSellItemListItemList () {
        return getForced(KEY_SELLITEMLIST_ITEMS, new ArrayList<String>());
    }
    
    public boolean isBuyItemListBlackList () {
        return getForced(KEY_BUYITEMLIST_BLACKLIST, true);
    }
    
    public List<String> getBuyItemListItemList () {
        return getForced(KEY_BUYITEMLIST_ITEMS, new ArrayList<String>());
    }
    
    public List<String> getWorldsBlacklisted () {
        return getForced(KEY_WORLDBLACKLIST, new ArrayList<String>());
    }
}
