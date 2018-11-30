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
        return getForced(KEY_SAVE_INTERVAL, 60L);
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
                Material.ACACIA_LEAVES.toString(),
                Material.OAK_LEAVES.toString(),
                Material.JUNGLE_LEAVES.toString(),
                Material.BIRCH_LEAVES.toString(),
                Material.DARK_OAK_LEAVES.toString(),
                Material.COBWEB.toString(),
                Material.TALL_GRASS.toString(),
                Material.DEAD_BUSH.toString(),
                Material.DANDELION.toString(),
                Material.SUNFLOWER.toString(),
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
                Material.LILY_PAD.toString(),
                Material.WATER.toString(),
                Material.RED_CARPET.toString(),
                Material.BROWN_CARPET.toString(),
                Material.BLACK_CARPET.toString(),
                Material.LIGHT_BLUE_CARPET.toString(),
                Material.BLUE_CARPET.toString(),
                Material.LIGHT_GRAY_CARPET.toString(),
                Material.GRAY_CARPET.toString(),
                Material.GREEN_CARPET.toString(),
                Material.LIME_CARPET.toString(),
                Material.YELLOW_CARPET.toString(),
                Material.ORANGE_CARPET.toString(),
                Material.MAGENTA_CARPET.toString(),
                Material.PURPLE_CARPET.toString(),
                Material.WHITE_CARPET.toString(),
                Material.CYAN_CARPET.toString(),
                Material.CARROT.toString(),
                Material.WHEAT.toString(),
                Material.PAINTING.toString(),
                Material.SIGN.toString(),
                Material.BLACK_BED.toString(),
                Material.BLUE_BED.toString(),
                Material.BROWN_BED.toString(),
                Material.CYAN_BED.toString(),
                Material.GRAY_BED.toString(),
                Material.GREEN_BED.toString(),
                Material.LIME_BED.toString(),
                Material.MAGENTA_BED.toString(),
                Material.ORANGE_BED.toString(),
                Material.PINK_BED.toString(),
                Material.PURPLE_BED.toString(),
                Material.RED_BED.toString(),
                Material.WHITE_BED.toString(),
                Material.YELLOW_BED.toString(),
                Material.LIGHT_BLUE_BED.toString(),
                Material.LIGHT_GRAY_BED.toString(),                Material.ITEM_FRAME.toString(),
                Material.FLOWER_POT.toString(),
                Material.SKELETON_SKULL.toString(),
                Material.SKELETON_WALL_SKULL.toString(),
                Material.WITHER_SKELETON_SKULL.toString(),
                Material.WITHER_SKELETON_WALL_SKULL.toString(),
                Material.ZOMBIE_HEAD.toString(),
                Material.PLAYER_HEAD.toString(),
                Material.ARMOR_STAND.toString(),
                Material.BLACK_BANNER.toString(),
                Material.BLUE_BANNER.toString(),
                Material.BROWN_BANNER.toString(),
                Material.RED_BANNER.toString(),
                Material.WHITE_BANNER.toString(),
                Material.CYAN_BANNER.toString(),
                Material.LIME_BANNER.toString(),
                Material.LIGHT_BLUE_BANNER.toString(),
                Material.LIGHT_GRAY_BANNER.toString(),
                Material.MAGENTA_BANNER.toString(),
                Material.PURPLE_BANNER.toString(),
                Material.GRAY_BANNER.toString(),
                Material.YELLOW_BANNER.toString(),
                Material.ORANGE_BANNER.toString(),
                Material.GREEN_BANNER.toString(),
                Material.LIGHT_BLUE_BANNER.toString(),
                Material.LIGHT_GRAY_BANNER.toString(),
                Material.BLACK_WALL_BANNER.toString(),
                Material.BLUE_WALL_BANNER.toString(),
                Material.BROWN_WALL_BANNER.toString(),
                Material.CYAN_WALL_BANNER.toString(),
                Material.GRAY_WALL_BANNER.toString(),
                Material.GREEN_WALL_BANNER.toString(),
                Material.LIME_WALL_BANNER.toString(),
                Material.MAGENTA_WALL_BANNER.toString(),
                Material.ORANGE_WALL_BANNER.toString(),
                Material.PINK_WALL_BANNER.toString(),
                Material.PURPLE_WALL_BANNER.toString(),
                Material.RED_WALL_BANNER.toString(),
                Material.WHITE_WALL_BANNER.toString(),
                Material.YELLOW_WALL_BANNER.toString(),
                Material.LIGHT_BLUE_WALL_BANNER.toString(),
                Material.LIGHT_GRAY_WALL_BANNER.toString(),
                Material.TNT.toString(),
                Material.HEAVY_WEIGHTED_PRESSURE_PLATE.toString(),
                Material.LIGHT_WEIGHTED_PRESSURE_PLATE.toString(),
                Material.STONE_PRESSURE_PLATE.toString(),
                Material.ACACIA_PRESSURE_PLATE.toString(),
                Material.OAK_PRESSURE_PLATE.toString(),
                Material.JUNGLE_PRESSURE_PLATE.toString(),
                Material.BIRCH_PRESSURE_PLATE.toString(),
                Material.DARK_OAK_PRESSURE_PLATE.toString(),
                Material.SPRUCE_PRESSURE_PLATE.toString(),
                Material.OAK_TRAPDOOR.toString(),
                Material.DARK_OAK_TRAPDOOR.toString(),
                Material.JUNGLE_TRAPDOOR.toString(),
                Material.ACACIA_TRAPDOOR.toString(),
                Material.BIRCH_TRAPDOOR.toString(),
                Material.SPRUCE_TRAPDOOR.toString(),
                Material.IRON_TRAPDOOR.toString(),
                Material.STONE_BUTTON.toString(),
                Material.OAK_BUTTON.toString(),
                Material.DARK_OAK_BUTTON.toString(),
                Material.ACACIA_BUTTON.toString(),
                Material.JUNGLE_BUTTON.toString(),
                Material.SPRUCE_BUTTON.toString(),
                Material.BIRCH_BUTTON.toString(),
                Material.TRIPWIRE_HOOK.toString(),
                Material.TRIPWIRE.toString(),
                Material.DAYLIGHT_DETECTOR.toString(),
                // TODO Material.DAYLIGHT_DETECTOR_INVERTED.toString(),
                Material.REDSTONE_BLOCK.toString(),
                Material.COMPARATOR.toString(),
                Material.REDSTONE_WIRE.toString(),
                Material.REDSTONE_TORCH.toString(),
                Material.REDSTONE_WALL_TORCH.toString(),
                Material.REPEATER.toString(),
                Material.ACACIA_DOOR.toString(),
                Material.BIRCH_DOOR.toString(),
                Material.IRON_DOOR.toString(),
                Material.JUNGLE_DOOR.toString(),
                Material.DARK_OAK_DOOR.toString(),
                Material.OAK_DOOR.toString(),
                Material.SPRUCE_DOOR.toString(),
                Material.RAIL.toString(),
                Material.ACTIVATOR_RAIL.toString(),
                Material.DETECTOR_RAIL.toString(),
                Material.POWERED_RAIL.toString(),
                Material.AIR.toString(),
                Material.CACTUS.toString(),
                Material.CAKE.toString(),
                Material.FIRE.toString(),
                Material.OAK_FENCE_GATE.toString(),
                Material.MELON_STEM.toString(),
                Material.WHEAT.toString(),
                Material.BEETROOT_SEEDS.toString(),
                Material.MELON_SEEDS.toString(),
                Material.PUMPKIN_SEEDS.toString(),
                Material.BEETROOT_SEEDS.toString(),
                Material.WHEAT_SEEDS.toString(),
                Material.PISTON.toString(),
                Material.PISTON_HEAD.toString(),
                Material.MOVING_PISTON.toString(),
                Material.STICKY_PISTON.toString(),
                Material.ROSE_RED.toString(),
                Material.ROSE_BUSH.toString(),
                Material.SPRUCE_SAPLING.toString(),
                Material.OAK_SAPLING.toString(),
                Material.ACACIA_SAPLING.toString(),
                Material.DARK_OAK_SAPLING.toString(),
                Material.BIRCH_SAPLING.toString(),
                Material.JUNGLE_SAPLING.toString(),
                Material.POTTED_ACACIA_SAPLING.toString(),
                Material.POTTED_BIRCH_SAPLING.toString(),
                Material.POTTED_JUNGLE_SAPLING.toString(),
                Material.POTTED_OAK_SAPLING.toString(),
                Material.POTTED_SPRUCE_SAPLING.toString(),
                Material.POTTED_DARK_OAK_SAPLING.toString(),
                Material.LAVA.toString(),
                Material.WATER.toString(),
                Material.FURNACE.toString(),
                Material.DISPENSER.toString(),
                Material.CRAFTING_TABLE.toString(),
                Material.DRAGON_EGG.toString(),
                Material.EGG.toString(),
                Material.TURTLE_EGG.toString(),
                Material.NETHER_WART.toString(),
                Material.NETHER_WART_BLOCK.toString(),
                Material.ENCHANTING_TABLE.toString(),
                Material.BREWING_STAND.toString(),
                Material.CAULDRON.toString(),
                Material.END_PORTAL.toString(),
                Material.END_PORTAL_FRAME.toString(),
                Material.END_STONE.toString(),
                Material.GRAVEL.toString(),
                Material.SAND.toString(),
                Material.SOUL_SAND.toString(),
                Material.DRAGON_EGG.toString(),
                Material.JUKEBOX.toString(),
                Material.PUMPKIN_STEM.toString(),
                Material.CHEST.toString(),
                Material.SPAWNER.toString(),
                Material.NOTE_BLOCK.toString(),
                Material.SIGN.toString(),
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
        return getForced(KEY_SELLITEMLIST_ITEMS, new ArrayList<>());
    }
    
    public boolean isBuyItemListBlackList () {
        return getForced(KEY_BUYITEMLIST_BLACKLIST, true);
    }
    
    public List<String> getBuyItemListItemList () {
        return getForced(KEY_BUYITEMLIST_ITEMS, new ArrayList<>());
    }
    
    public List<String> getWorldsBlacklisted () {
        return getForced(KEY_WORLDBLACKLIST, new ArrayList<>());
    }
}
