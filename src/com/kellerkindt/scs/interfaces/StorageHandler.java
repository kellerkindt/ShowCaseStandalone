/**
* ShowCaseStandalone
* Copyright (C) 2012 Kellerkindt <copyright at kellerkindt.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
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
     * @param elements {@link Collection} to save the elements of
     * @throws IOException On an critical issue, preventing further saving
     */
    void save(Collection<E> elements) throws IOException;

    /**
     * @param element Element to save
     * @throws IOException On any issue while saving the given element
     */
    void save(E element) throws IOException;

    /**
     * @param element Element to delete
     * @throws IOException On any issue while deleting the given element
     */
    void delete(E element) throws IOException;
    
    /**
     * Writes all cached data to the {@link StorageHandler}'s destination
     * @throws IOException On an critical issue, preventing further flushing
     */
    void flush() throws IOException;
}
