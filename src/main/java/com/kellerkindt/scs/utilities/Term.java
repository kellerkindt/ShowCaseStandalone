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
package com.kellerkindt.scs.utilities;

public enum Term {
    ABORT,
    DISABLE,
    NEXT,
    PRUNE,
    WARNING_DEV_VERSION,
    
    ERROR,
    ERROR_COMMAND_UNKNOWN,
    ERROR_IMPORT,
    ERROR_GENERAL,
    ERROR_ON_SAVE,
    ERROR_ALREADY_SHOWCASE,
    ERROR_BUY_LIMIT,
    ERROR_SET_OWNER_OFFLINE,
    ERROR_SET_PRICE_DISPLAY,
    ERROR_GET_DISPLAY,
    ERROR_ADD_ITEMS_DISPlAY,
    ERROR_ADD_ITEMS_UNLIMITED,
    ERROR_REM_ITEMS_UNLIMITED,
    ERROR_CREATE_UNLIMITED,
    ERROR_EXECUTE_AS_PLAYER,
    ERROR_INSUFFICIENT_ITEMS_CREATE,
    ERROR_INSUFFICIENT_ITEMS_EXCHANGE,
    ERROR_INSUFFICIENT_MONEY_CREATE,
    ERROR_INSUFFICIENT_MONEY_OWNER,
    ERROR_INSUFFICIENT_MONEY_COSTUMER,
    ERROR_INSUFFICIENT_MONEY_YOU,
    ERROR_INSUFFICIENT_ROOM_BUY,
    ERROR_INSUFFICIENT_ROOM,
    ERROR_INSUFFICIENT_PERMISSION,
    ERROR_INSUFFICIENT_PERMISSION_REGION,
    ERROR_INSUFFICIENT_PERMISSION_SET_PRICE,
    ERROR_INSUFFICIENT_PERMISSION_SET_LIMIT,
    ERROR_INSUFFICIENT_PERMISSION_ADD_ITEM,
    ERROR_INSUFFICIENT_PERMISSION_GET_ITEM,
    ERROR_INSUFFICIENT_PERMISSION_REM_SHOWCASE,
    ERROR_INSUFFICIENT_PERMISSION_SET_OWNER,
    ERROR_INSUFFICIENT_PERMISSION_ADD_MEMBER,
    ERROR_INSUFFICIENT_PERMISSION_REM_MEMBER,
    ERROR_INSUFFICIENT_PERMISSION_DESTROY,
    ERROR_MISSING_OR_INCORRECT_ARGUMENT,
    ERROR_PRICE_NEGATIVE,
    ERROR_PRICE_NOT_IN_RANGE,
    ERROR_TRANSACTION_NO_RECORDED,
    ERROR_UNDO_NO_AVAILABLE,
    ERROR_UNDO_EXPIRED,
    ERROR_UNDO_TWICE,
    ERROR_UNDO_BUY_ITEM,
    ERROR_UNDO_BUY_MONEY,
    ERROR_UNDO_SELL_ITEM,
    ERROR_UNDO_SELL_MONEY,
    ERROR_UNDO_UNKNOWN,
    ERROR_AREA_PROTECTED,
    ERROR_REQUIRE_OBJECT,
    ERROR_FULL_SHOWCASE_COSTUMER,
    ERROR_FULL_SHOWCASE_OWNER,
    ERROR_USING_ALREADY,
    ERROR_NOT_A_SHOP,
    ERROR_CURRENTLY_INVENTORY_OPENED,
    ERROR_PURGE_ZERO_SHOPS,
    ERROR_SHOP_LIMIT_EXCEEDED,
    
    SIGN_INVENTORY,
    SIGN_PRICE,
    SIGN_UNLIMITED,
    
    
    BLACKLIST_WORLD,
    BLACKLIST_ITEM,
    BLACKLIST_BLOCK,
    
    COLOR_BUY,
    COLOR_INACTIVE,
    COLOR_SELL,
    COLOR_EXCHANGE,
    
    
    MESSAGE_BUY,
    MESSAGE_BUY_OWNER_1,
    MESSAGE_BUY_OWNER_2,
    MESSAGE_BUY_LIMIT,
    MESSAGE_UNIT,
    MESSAGE_EXPLODED,
    MESSAGE_SET_PRICE,
    MESSAGE_SET_OWNER,
    MESSAGE_SET_UNIT,
    MESSAGE_RECEIVED_ITEMS,
    MESSAGE_RELOADING,
    MESSAGE_SELL_COSTUMER,
    MESSAGE_SELL_OWNER_1,
    MESSAGE_SELL_OWNER_2,
    MESSAGE_PRICERANGE,
    MESSAGE_PRICERANGE_REMOVED,
    MESSAGE_PRICERANGE_GLOBAL,
    MESSAGE_PRICERANGE_UPDATED,
    MESSAGE_PRICERANGE_UPDATED_GLOBAL,
    MESSAGE_SUCCESSFULL_CREATED,
    MESSAGE_SUCCESSFULL_REMOVED,
    MESSAGE_SUCCESSFULL_UNDID,
    MESSAGE_SUCCESSFULL_DESTROYED,
    MESSAGE_SUCCESSFULL_ADDED_MEMBER,
    MESSAGE_SUCCESSFULL_REMOVED_MEMBER,
    
    MESSAGE_LIST,
    MESSAGE_PURGE_FOUND,
    MESSAGE_PURGE_DELETED,
//    MESSAGE_LIST_COUNT_OWNER,
//    MESSAGE_LIST_COUNT_MEMBER,
//    MESSAGE_LIST_WORLDS,
    
    MESSAGE_ABOUT,
    MESSAGE_VERSION_1,
    MESSAGE_VERSION_2,
    MESSAGE_VERSION_3,
    MESSAGE_VERSION_4,
    MESSAGE_VERSION_5,
    
    
    
    SHOP_PRICE_CREATE,
    SHOP_EMPTY_COSTUMER,
    SHOP_EMPTY_OWNER,
    
    IGNORE_RECEIVE,
    IGNORE_TRANSACTION,
    
    INVENTORY_CURRENT,
    INVENTORY_FULL,
    INVENTORY_UPDATE,
    
    ITEM_DELIMITER,
    ITEM_ON_DISPLAY,
    ITEM_MISSING,
    ITEM_LEFT,
    ITEM_NOT_MATCHING,
    
    
    
    HELP_1,
    HELP_2,
    HELP_3,
    HELP_4,
    HELP_5,
    HELP_6,
    HELP_7,
    HELP_8,
    HELP_9,
    HELP_10,
    HELP_11,
    HELP_12,
    HELP_13,
    HELP_14,
    HELP_15,
    HELP_16,
    HELP_17,
    HELP_18,
    HELP_19,
    HELP_20,
    HELP_21,
    HELP_22,
    HELP_23,
    HELP_24,
    HELP_25,
    HELP_26,
    HELP_27,    // member, shown after HELP_10
    HELP_28,    // about
    HELP_29,    // version
    HELP_30,    // range
    HELP_31,    // hover text
    HELP_ADMIN_1,
    HELP_ADMIN_2,
    HELP_ADMIN_3,
    HELP_ADMIN_4,
    HELP_ADMIN_5,
    HELP_ADMIN_6,
    HELP_ADMIN_7,
    HELP_ADMIN_8,    // purge
    HELP_ADMIN_9,    // report
    HELP_ADMIN_10,    // repair
    HELP_TITLE,
    HELP_TITLE_ADMIN,

    INFO_SHOP_SELL,
    INFO_SHOP_SELL_UNLIMITED,
    INFO_SHOP_BUY,
    INFO_SHOP_BUY_UNLIMITED,
    INFO_SHOP_EXCHANGE,
    INFO_SHOP_EXCHANGE_UNLIMITED,
    INFO_SHOP_EXCHANGE_SPECIFY_ITEM,
    INFO_SHOP_DISPLAY,
    INFO_SHOP_ENCHANTMENTS,
    INFO_SHOP_ENCHANTMENT,

    INFO_SHOP_BY_PLAYER,
    INFO_SHOP_STOCK_CURRENT,
    INFO_SHOP_STOCK_BUYING,

    // INFO_1,
    // INFO_2,
    // INFO_3,
    // INFO_4,
    // INFO_5,
    // INFO_6,
    // INFO_7,
    // INFO_8,
    // INFO_9,
    // INFO_10,
    // INFO_11,
    // INFO_12,
    // INFO_13,
    // INFO_14,
    // INFO_15,
    // INFO_16,

    INFO_UNLIMITED,
    
    INFO_UNDO_1,
    INFO_UNDO_2,
    INFO_UNDO_3,
    INFO_UNDO_4,
    INFO_UNDO_5,
    INFO_UNDO_6,
    INFO_UNDO_7,
    INFO_UNDO_8,
    
    REPAIR_INFO,
    REPAIR_HELP_1,
    REPAIR_HELP_2,
    RESTORE_START,
    RESTORE_END,
    DELETE_START,
    DELETE_END,

    /**
     * The custom name value for the item
     * that is floating above the shop
     *
     * %1   material name
     * %2   formatted price
     * %3   shop owner
     * %4   shop amount
     * %5   whether unlimited
     */
    SHOP_ITEM_CUSTOM_NAME_TYPE_SELL,

    /**
     * The custom name value for the item
     * that is floating above the shop
     *
     * %1   material name
     * %2   formatted price
     * %3   shop owner
     * %4   shop amount
     * %5   whether unlimited
     */
    SHOP_ITEM_CUSTOM_NAME_TYPE_BUY,

    /**
     * The custom name value for the item
     * that is floating above the shop
     *
     * %1   material name
     * %2   formatted price
     * %3   shop owner
     * %4   shop amount
     * %5   whether unlimited
     * %6   exchange material name
     */
    SHOP_ITEM_CUSTOM_NAME_TYPE_EXCHANGE,

    /**
     * The custom name value for the item
     * that is floating above the shop
     *
     * %1   material name
     * %2   formatted price
     * %3   shop owner
     * %4   shop amount
     * %5   whether unlimited
     */
    SHOP_ITEM_CUSTOM_NAME_TYPE_DISPLAY
    ;
    
    private String term        = null;
    
    private static final String    indicator    = "%";
    private static final String missing        = "`RTERM: MISSING VALUE FOR ";
    
    /**
     * @param value    Values to insert
     * @return        Adjusted string
     */
    public String get (String ...value) {
        String     term    = this.term;
        
        if (!isSet()) {
            return missing + this.toString();
        }
        
        for (int i = 0; i < value.length; i++) {
            term = term.replace(indicator+(i+1), value[i] != null ? value[i] : "");
        }
        
        return term;
    }
    
    /**
     * @return true if a term was set, false if nothing is set
     */
    public boolean isSet () {
        return (term != null);
    }
    
    /**
     * Sets the Language and value of this Term
     * @param term
     */
    public void setTerm (String term) {
        this.term        = term;
    }
}
