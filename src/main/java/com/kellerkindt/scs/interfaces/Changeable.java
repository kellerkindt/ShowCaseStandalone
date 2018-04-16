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

public interface Changeable<T extends Changeable<?>> {

    /**
     * Sets the state of this to unchanged
     */
    void resetHasChanged ();
    
    /**
     * @return Whether this has changed since the last {@link #resetHasChanged()}
     */
    boolean hasChanged ();

    /**
     * Disables all {@link ChangeListener} notifications
     * while invoking the given {@link Runnable}, contacting
     * the {@link ChangeListener}s only once in total, regardless
     * of the amount of changes.
     *
     * @param runnable {@link Runnable} to execute before contacting any {@link ChangeListener}s
     * @return The {@link #hasChanged()} state of this {@link Changeable}
     */
    boolean bulkChanges(Runnable runnable);

    /**
     * @param listener {@link ChangeListener} to notify on changes
     */
    void addChangeListener(ChangeListener<T> listener);

    /**
     * @param listener {@link ChangeListener} to no longer notify on changes
     */
    void removeChangeListener(ChangeListener<T> listener);



    /**
     * @param <T> The implementation of {@link Changeable} to get notified for
     */
    interface ChangeListener<T extends Changeable> {

        /**
         * @param changeable {@link Changeable} that has changed
         */
        void onChanged(T changeable);
    }
}
