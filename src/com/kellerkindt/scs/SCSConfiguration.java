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
package com.kellerkindt.scs;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

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
    public static final String KEY_ACCESSTHROUGHINVENTORY   = "AccessThroughInventory";
    
    public static final String KEY_CREATEPRICE_BUYSHOP      = "CreatePrice.BuyShop";
    public static final String KEY_CREATEPRICE_SELLSHOP     = "CreatePrice.SellShop";
    public static final String KEY_CREATEPRICE_DISPLAY      = "CreatePrice.Display";
    public static final String KEY_CREATEPRICE_EXCHANGE     = "CreatePrice.Exchange";

    public static final String KEY_HOVER_TEXT_ENABLED                       = "HoverText.Enabled";
    public static final String KEY_HOVER_TEXT_PLAYER_CUSTOM_NAME_ENABLED    = "HoverText.PlayerCustomNameEnabled";
    public static final String KEY_HOVER_TEXT_PLAYER_CUSTOM_NAME_MAX_LENGTH = "HoverText.PlayerCustomNameMaxLength";
    public static final String KEY_HOVER_TEXT_BALANCE_MAX_LENGTH            = "HoverText.BalanceMaxLength";
    public static final String KEY_HOVER_TEXT_BALANCE_FORMATTER             = "HoverText.BalanceFormatter";
    
    public static final String KEY_SAVE_INTERVAL            = "Save.Interval";
    
    public static final String KEY_DISPLAY_USESIGNS         = "Display.UseSigns";
    public static final String KEY_DISPLAY_USESTORAGE       = "Display.UseStorage";

    public static final String KEY_SPAWNING_COUNT           = "Spawning.Count";
    public static final String KEY_SPAWNING_TO_MAX          = "Spawning.ToMax";
    
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
    public static final String KEY_DEBUG_SHOP_CREATION      = "Debug.Shop.Creation";

    public static final String KEY_DISABLE_FBASIC_ANTIDUPE  = "Disable.FBasicsInventoryDupeListener";

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
        rename("DefaultUnit",                       "Default.Unit");                     // 2014-03-02
        rename("DefaultShowTransactionMessages",    "Default.ShowTransactionMessages"); // 2014-03-02
        rename("Visible.CustomName",                "HoverText.Enabled");               // 2016-11-28
        
        
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

    public boolean isHoverTextEnabled() {
        return getForced(KEY_HOVER_TEXT_ENABLED, true);
    }

    public boolean isHoverTextPlayerCustomNameEnabled() {
        return getForced(KEY_HOVER_TEXT_PLAYER_CUSTOM_NAME_ENABLED, true);
    }

    public int getHoverTextPlayerCustomNameMaxLength() {
        return getForced(KEY_HOVER_TEXT_PLAYER_CUSTOM_NAME_MAX_LENGTH, 32);
    }

    /**
     * @return The maximum allowed length of the hover text if formatted by the balance hook
     */
    public int getHoverTextBalanceMaxLength() {
        return getForced(KEY_HOVER_TEXT_BALANCE_MAX_LENGTH, 7);
    }

    public String getHoverTextBalanceFormatter() {
        return getForced(KEY_HOVER_TEXT_BALANCE_FORMATTER, "%.2f$");
    }

    public boolean isDebuggingSave() {
        return getForced(KEY_DEBUG_SAVE, false);
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

    public boolean isDebuggingShopCreation() {
        return getForced(KEY_DEBUG_SHOP_CREATION, false);
    }
    
    public boolean isDisplayShopUsingSigns () {
        return getForced(KEY_DISPLAY_USESIGNS, true);
    }
    
    public boolean isDisplayShopUsingStorage () {
        return getForced(KEY_DISPLAY_USESTORAGE, true);
    }

    public boolean isSpawningToMax() {
        return getForced(KEY_SPAWNING_TO_MAX, false);
    }

    public int getSpawnCount() {
        return getForced(KEY_SPAWNING_COUNT, 0);// 0 makes it not pickupable!! o.O
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

    public boolean isDisablingFBasicsInventoryDupeListener() {
        return getForced(KEY_DISABLE_FBASIC_ANTIDUPE, true);
    }
    
    public String getLocalizationFile () {
        return getForced(KEY_LOCALIZATION_FILE, getLocalizationFileDefault());
    }

    public String getLocalizationFileDefault() {
        return "locale_EN.yml";
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
    
    public Collection<String> getBlockListBlocks () {
        // somewhere along the way the list got wiped,
        // so empty lists are going to be refilled
        List<String> list = getForced(KEY_BLOCKLIST_BLOCKS, Collections.emptyList());
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return getForced(KEY_BLOCKLIST_BLOCKS, Arrays.asList(
                Material.LEAVES.toString(),
                Material.LEAVES_2.toString(),
                Material.WEB.toString(),
                Material.LONG_GRASS.toString(),
                Material.DEAD_BUSH.toString(),
                Material.YELLOW_FLOWER.toString(),
                Material.CHORUS_FLOWER.toString(),
                Material.BROWN_MUSHROOM.toString(),
                Material.RED_MUSHROOM.toString(),
                Material.TORCH.toString(),
                Material.LADDER.toString(),
                Material.SNOW.toString(),
                Material.SNOW_BLOCK.toString(),
                Material.ICE.toString(),
                Material.LAVA.toString(),
                Material.VINE.toString(),
                Material.WATER_LILY.toString(),
                Material.WATER.toString(),
                Material.CARPET.toString(),
                Material.CARROT.toString(),
                Material.WHEAT.toString(),
                Material.DOUBLE_PLANT.toString(),
                Material.PAINTING.toString(),
                Material.SIGN.toString(),
                Material.BED.toString(),
                Material.ITEM_FRAME.toString(),
                Material.FLOWER_POT.toString(),
                Material.SKULL.toString(),
                Material.ARMOR_STAND.toString(),
                Material.BANNER.toString(),
                Material.TNT.toString(),
                Material.GOLD_PLATE.toString(),
                Material.IRON_PLATE.toString(),
                Material.STONE_PLATE.toString(),
                Material.WOOD_PLATE.toString(),
                Material.TRAP_DOOR.toString(),
                Material.IRON_TRAPDOOR.toString(),
                Material.STONE_BUTTON.toString(),
                Material.WOOD_BUTTON.toString(),
                Material.TRIPWIRE_HOOK.toString(),
                Material.TRIPWIRE.toString(),
                Material.DAYLIGHT_DETECTOR.toString(),
                Material.DAYLIGHT_DETECTOR_INVERTED.toString(),
                Material.REDSTONE_BLOCK.toString(),
                Material.REDSTONE_COMPARATOR.toString(),
                Material.REDSTONE_COMPARATOR_ON.toString(),
                Material.REDSTONE_COMPARATOR_OFF.toString(),
                Material.REDSTONE_WIRE.toString(),
                Material.REDSTONE_TORCH_ON.toString(),
                Material.REDSTONE_TORCH_OFF.toString(),
                Material.DIODE.toString(),
                Material.DIODE_BLOCK_ON.toString(),
                Material.DIODE_BLOCK_OFF.toString(),
                Material.ACACIA_DOOR.toString(),
                Material.BIRCH_DOOR.toString(),
                Material.IRON_DOOR.toString(),
                Material.JUNGLE_DOOR.toString(),
                Material.WOOD_DOOR.toString(),
                Material.WOODEN_DOOR.toString(),
                Material.SPRUCE_DOOR.toString(),
                Material.RAILS.toString(),
                Material.ACTIVATOR_RAIL.toString(),
                Material.DETECTOR_RAIL.toString(),
                Material.POWERED_RAIL.toString(),
                Material.AIR.toString(),
                Material.CACTUS.toString(),
                Material.CAKE_BLOCK.toString(),
                Material.CROPS.toString(),
                Material.FIRE.toString(),
                Material.FENCE_GATE.toString(),
                Material.MELON_STEM.toString(),
                Material.PISTON_BASE.toString(),
                Material.PISTON_EXTENSION.toString(),
                Material.PISTON_MOVING_PIECE.toString(),
                Material.PISTON_STICKY_BASE.toString(),
                Material.RED_ROSE.toString(),
                Material.SAPLING.toString(),
                Material.STATIONARY_WATER.toString(),
                Material.STATIONARY_LAVA.toString(),
                Material.BURNING_FURNACE.toString(),
                Material.FURNACE.toString(),
                Material.DISPENSER.toString(),
                Material.WORKBENCH.toString(),
                Material.MONSTER_EGG.toString(),
                Material.MONSTER_EGGS.toString(),
                Material.NETHER_WARTS.toString(),
                Material.NETHER_WART_BLOCK.toString(),
                Material.ENCHANTMENT_TABLE.toString(),
                Material.BREWING_STAND.toString(),
                Material.CAULDRON.toString(),
                Material.ENDER_PORTAL.toString(),
                Material.ENDER_PORTAL_FRAME.toString(),
                Material.ENDER_STONE.toString(),
                Material.GRAVEL.toString(),
                Material.SAND.toString(),
                Material.SOUL_SAND.toString(),
                Material.DRAGON_EGG.toString(),
                Material.JUKEBOX.toString(),
                Material.PUMPKIN_STEM.toString(),
                Material.CHEST.toString(),
                Material.MOB_SPAWNER.toString(),
                Material.NOTE_BLOCK.toString(),
                Material.SIGN_POST.toString(),
                Material.WALL_SIGN.toString()
        ));
    }
    
    public boolean isSellItemListBlacklist () {
        return getForced(KEY_SELLITEMLIST_BLACKLIST, true);
    }
    
    public boolean hasAccessThroughInventory() {
        return getForced(KEY_ACCESSTHROUGHINVENTORY, true);
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
