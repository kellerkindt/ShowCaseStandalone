/*
 * Copyright (c) 2016-01-03 17:00 +01, kellerkindt <copyright at kellerkindt.com>
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
