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

import java.io.IOException;
import java.util.Collection;


public interface StorageHandler<E> extends Threaded {
    
    /**
     * Loads as many elements as possible
     * @return {@link Collection} of loaded elements
     * @throws IOException On an critical issue, preventing further loading
     */
    Collection<E> loadAll() throws IOException;

    /**
     * Enqueues the given elements to be saved
     *
     * @param elements {@link Iterable} to save the elements of
     */
    void save(Iterable<E> elements);

    /**
     * Enqueues the given element to be saved
     *
     * @param element Element to save
     * @return given element
     */
    E save(E element);

    /**
     * Enqueues the given element to be deleted
     *
     * @param element Element to delete
     */
    void delete(E element);
    
    /**
     * Writes all cached data to the {@link StorageHandler}'s destination
     * @throws IOException On an critical issue, preventing further flushing
     */
    void flush() throws IOException;
}
