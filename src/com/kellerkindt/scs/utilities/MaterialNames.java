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

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


/**
 * @author Pasukaru from:
 * http://forums.bukkit.org/threads/how-to-convert-an-item-id-into-a-normal-name.23698/#post-430168
 * Updated by Sorklin.
 * updated by kellerkindt 2013-02-12
 */
public class MaterialNames {


    public static String getItemName(ItemStack stack) {
        if (stack.getItemMeta().getDisplayName() == null || stack.getItemMeta().getDisplayName().isEmpty()) {
            return stack.getType().name();
        } else {
            return stack.getItemMeta().getDisplayName();
        }
    }
}
