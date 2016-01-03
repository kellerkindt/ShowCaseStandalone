/*
 * Copyright (c) 2016-01-03 17:05 +01, kellerkindt <copyright at kellerkindt.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
        this.listeners = new ArrayList<ChangeListener<T>>();
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
