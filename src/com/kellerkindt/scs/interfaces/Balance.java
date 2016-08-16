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
package com.kellerkindt.scs.interfaces;

import com.kellerkindt.scs.internals.NamedUUID;
import org.bukkit.OfflinePlayer;

import java.util.UUID;



public interface Balance {

    /**
     * @return Whether this {@link Balance} instance is active / usable
     */
    boolean isActive();

    /**
     * @param player {@link OfflinePlayer} to check
     * @return Whether the mentioned player has exists in the underlying balance system
     */
    default boolean exists(OfflinePlayer player) {
        return exists(
                player,
                player == null ? null : player.getUniqueId(),
                player == null ? null : player.getName()
        );
    }

    /**
     * @param namedUUID The {@link NamedUUID} containing the name and/or {@link UUID} of the player to check
     * @return Whether the mentioned player has exists in the underlying balance system
     */
    default boolean exists(NamedUUID namedUUID) {
        return exists(
                null,
                namedUUID == null ? null : namedUUID.getId(),
                namedUUID == null ? null : namedUUID.getName()
        );
    }

    /**
     *
     * @param playerId The {@link UUID} of the player to check
     * @return Whether the mentioned player has exists in the underlying balance system
     */
    default boolean exists(UUID playerId) {
        return exists(
                null,
                playerId,
                null
        );
    }

    /**
     * @deprecated try to avoid operation solely on the name of the player
     * @param playerName The name of the player to check
     * @return Whether the mentioned player has exists in the underlying balance system
     */
    default boolean exists(String playerName) {
        return exists(
                null,
                null,
                playerName
        );
    }

    /**
     * Either the {@link UUID} or name has to be not null,
     * also see the note at {@link #has(String, double)}
     *
     * @param playerId The {@link UUID} of the player to check or null
     * @param playerName The name of the player to check or null
     * @return Whether the mentioned player has exists in the underlying balance system
     */
    default boolean exists(UUID playerId, String playerName) {
        return  exists(
                null,
                playerId,
                playerName
        );
    }

    /**
     * Checks whether the player for either the {@link OfflinePlayer} instance,
     * {@link UUID} or name does exist / is known to the underlying balance system.
     *
     * @param player {@link OfflinePlayer} to check or null
     * @param playerId The {@link UUID} of the player to check or null
     * @param playerName The name of the player to check or null
     * @return Whether the mentioned player has exists in the underlying balance system
     */
    boolean exists(OfflinePlayer player, UUID playerId, String playerName);






    /**
     * @param player The {@link OfflinePlayer} to check the balance for
     * @param amount The amount to check for
     * @return Whether the given {@link OfflinePlayer} has the given amount of balance
     */
    default boolean has(OfflinePlayer player, double amount) {
        return has(
                player,
                player == null ? null : player.getUniqueId(),
                player == null ? null : player.getName(),
                amount
        );
    }

    /**
     * @param namedUUID The {@link NamedUUID} containing the name and/or {@link UUID} of the player to check for
     * @param amount The amount to check for
     * @return Whether the player for the given {@link NamedUUID} has the given amount of balance
     */
    default boolean has(NamedUUID namedUUID, double amount) {
        return has(
                null,
                namedUUID == null ? null : namedUUID.getId(),
                namedUUID == null ? null : namedUUID.getName(),
                amount
        );
    }

    /**
     * @param playerId The {@link UUID} of the player to check the balance for
     * @param amount The amount to check for
     * @return Whether the player for the given {@link UUID} has the given amount of balance
     */
    default boolean has(UUID playerId, double amount) {
        return has(
                null,
                playerId,
                null,
                amount
        );
    }

    /**
     * @deprecated try to avoid operation solely on the name of the player
     * @param playerName The name of the player to check for
     * @param amount The amount to check for
     * @return Whether the player of the given name has the given amount of balance
     */
    default boolean has(String playerName, double amount) {
        return has(
                null,
                null,
                playerName,
                amount
        );
    }

    /**
     * Either the {@link UUID} or name has to be not null,
     * also see the note at {@link #has(String, double)}
     *
     * @param playerId The {@link UUID} of the player to check for or null
     * @param playerName The name of the player to check for or null
     * @param amount The amount to check for
     * @return Whether the player for the given {@link UUID} and/or name has the given amount of balance
     */
    default boolean has(UUID playerId, String playerName, double amount) {
        return  has(
                null,
                playerId,
                playerName,
                amount
        );
    }

    /**
     * Checks whether the player for either the {@link OfflinePlayer} instance,
     * {@link UUID} or name has the given amount of bank balance in that order.
     * At least one of that parameters has to be not null
     *
     * @param player {@link OfflinePlayer} to check the balance for or null
     * @param playerId The {@link UUID} of the player to check the balance for or null
     * @param playerName The name of the player to check the balance for or null
     * @param amount The amount to check for
     * @return Whether the mentioned player has the given amount of balance
     */
    boolean has(OfflinePlayer player, UUID playerId, String playerName, double amount);

    /**
     * @param player The {@link OfflinePlayer} to add the amount to
     * @param amount The amount to add to the balance of the player
     * @return Whether the given amount has been added, if false, the balance has not been altered
     */
    default boolean add(OfflinePlayer player, double amount) {
        return add(
                player,
                player == null ? null : player.getUniqueId(),
                player == null ? null : player.getName(),
                amount
        );
    }

    /**
     * @param namedUUID The {@link NamedUUID} containing the name and/or {@link UUID} of the player to add the amount to
     * @param amount The amount to add to the balance of the player
     * @return Whether the given amount has been added, if false, the balance has not been altered
     */
    default boolean add(NamedUUID namedUUID, double amount) {
        return add(
                null,
                namedUUID == null ? null : namedUUID.getId(),
                namedUUID == null ? null : namedUUID.getName(),
                amount
        );
    }

    /**
     * @param playerId The id of the player to add the amount to
     * @param amount The amount to add to the balance of the player
     * @return Whether the given amount has been added, if false, the balance has not been altered
     */
    default boolean add(UUID playerId, double amount) {
        return add(
                null,
                playerId,
                null,
                amount
        );
    }

    /**
     * @deprecated try to avoid operation solely on the name of the player
     * @param playerName The name of the player to add the amount to
     * @param amount The amount to add to the balance of the player
     * @return Whether the given amount has been added, if false, the balance has not been altered
     */
    default boolean add(String playerName, double amount) {
        return add(
                null,
                null,
                playerName,
                amount
        );
    }

    /**
     * Either the {@link UUID} or name has to be not null,
     * also see the note at {@link #add(String, double)}
     *
     * @param playerId The {@link UUID} of the player to add the amount to or null
     * @param playerName The name of the player to add the amount to or null
     * @param amount The amount to add to the balance of the player
     * @return Whether the given amount has been added, if false, the balance has not been altered
     */
    default boolean add(UUID playerId, String playerName, double amount) {
        return add(
                null,
                playerId,
                playerName,
                amount
        );
    }

    /**
     * Adds the given amount to the balance of the of either the {@link OfflinePlayer}
     * instance, {@link UUID} or name in that order.
     * At least one of that parameters has to be not null
     *
     * @param player The {@link OfflinePlayer} to add the amount to
     * @param playerId The {@link UUID} of the player to add the amount to
     * @param playerName The name of the player to add the amount to
     * @param amount The amount to add
     * @return Whether the given amount has been added, if false, the balance has not been altered
     */
    boolean add(OfflinePlayer player, UUID playerId, String playerName, double amount);


    /**
     * @param player The {@link OfflinePlayer} to subtract the amount from
     * @param amount The amount to subtract
     * @return Whether the given amount has been subtracted, if false, the balance has not been altered
     */
    default boolean sub(OfflinePlayer player, double amount) {
        return sub(
                player,
                player == null ? null : player.getUniqueId(),
                player == null ? null : player.getName(),
                amount
        );
    }

    /**
     * @param namedUUID The {@link NamedUUID} with the name and/or {@link UUID} of the player to subtract the amount from
     * @param amount The amount to subtract
     * @return Whether the given amount has been subtracted, if false, the balance has not been altered
     */
    default boolean sub(NamedUUID namedUUID, double amount) {
        return sub(
                null,
                namedUUID == null ? null : namedUUID.getId(),
                namedUUID == null ? null : namedUUID.getName(),
                amount
        );
    }

    /**
     * @param playerId The {@link UUID} of the player to subtract the amount from
     * @param amount The amount to subtract
     * @return Whether the given amount has been subtracted, if false, the balance has not been altered
     */
    default boolean sub(UUID playerId, double amount) {
        return sub(
                null,
                playerId,
                null,
                amount
        );
    }

    /**
     * @deprecated try to avoid operation solely on the name of the player
     * @param playerName The name of the player to subtract the amount from
     * @param amount The amount to subtract from the balance of the player
     * @return Whether the given amount has been subtracted, if false, the balance has not been altered
     */
    default boolean sub(String playerName, double amount) {
        return sub(
                null,
                null,
                playerName,
                amount
        );
    }

    /**
     * Either the {@link UUID} or name has to be not null,
     * also see the note at {@link #sub(String, double)}
     *
     * @param playerId The {@link UUID} of the player to subtract the amount from or null
     * @param playerName The name of the player  to subtract the amount from or null
     * @param amount The amount to subtract from the balance of the player
     * @return Whether the given amount has been subtracted, if false, the balance has not been altered
     */
    default boolean sub(UUID playerId, String playerName, double amount) {
        return sub(
                null,
                playerId,
                playerName,
                amount
        );
    }

    /**
     * Subtracts the given amount from the balance of the of either the {@link OfflinePlayer}
     * instance, {@link UUID} or name in that order.
     * At least one of that parameters has to be not null
     *
     * @param player The {@link OfflinePlayer} to subtract the amount from or null
     * @param playerId The {@link UUID} of the player to subtract the amount from or null
     * @param playerName The name of the player  to subtract the amount from or null
     * @param amount The amount to subtract from the balance of this player
     * @return Whether the given amount has been subtracted, if false, the balance has not been altered
     */
    boolean sub(OfflinePlayer player, UUID playerId, String playerName, double amount);

    /**
     * @param amount The amount to format as {@link String}
     * @return {@link String} of the given amount formatted as one would mention the amount in a sentence
     */
    String format(double amount);
}
