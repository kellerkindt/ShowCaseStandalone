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

package com.kellerkindt.scs.internals;

import com.kellerkindt.scs.interfaces.Changeable;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> The type of the child class
 */
public abstract class SimpleChangeable<T extends SimpleChangeable<?>> implements Changeable<T> {

    protected boolean changed;
    protected boolean contact;
    protected List<ChangeListener<T>> listeners;

    public SimpleChangeable() {
        this.changed   = false;
        this.contact   = true;
        this.listeners = new ArrayList<>();
    }

    @Override
    public boolean hasChanged() {
        return changed;
    }

    @Override
    public void resetHasChanged() {
        this.changed = false;
    }

    @Override
    public boolean bulkChanges(Runnable runnable) {
        boolean changed = this.changed;
        this.changed    = false;
        this.contact    = false;

        try {
            // try to do all the changes
            runnable.run();

        } finally {
            // check whether to notify the listeners
            if (this.changed) {
                this.notifyChangeListeners();
            }

            // be sure to enable notifications again
            this.contact = true;
            this.changed|= changed;

        }
        return hasChanged();
    }

    /**
     * Contacts the {@link ChangeListener}s
     * if the global notification is enabled
     *
     * @return itself
     */
    protected T setChanged() {
        return setChanged(true, null);
    }


    /**
     * Contacts the {@link ChangeListener}s
     * if the given condition is true and the global notification
     * is enabled
     *
     * @param condition Whether a value has changed
     * @return itself
     */
    protected T setChanged(boolean condition) {
        return setChanged(condition, null);
    }

    /**
     * Invokes the given {@link Runnable} if the given
     * condition is true and then contacts the {@link ChangeListener}s
     * if the given condition is true and the global notification
     * is enabled
     *
     * @param condition Whether a value has changed
     * @param runnable {@link Runnable} to call to update the value
     * @return itself
     */
    protected T setChanged(boolean condition, Runnable runnable) {
        this.changed |= condition;

        if (condition) {
            if (runnable != null) {
                runnable.run();
            }
            if (contact) {
                notifyChangeListeners();
            }
        }
        return (T)this;
    }

    /**
     * Notify all {@link ChangeListener}s that this {@link Changeable} has changed
     */
    protected void notifyChangeListeners() {
        for (ChangeListener<T> listener : listeners) {
            try {
                listener.onChanged((T)this);
            } catch (Throwable t) {
                // TODO is there a more elegant way?
                t.printStackTrace();
            }
        }
    }

    @Override
    public void addChangeListener(ChangeListener<T> listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeChangeListener(ChangeListener<T> listener) {
        this.listeners.remove(listener);
    }
}
