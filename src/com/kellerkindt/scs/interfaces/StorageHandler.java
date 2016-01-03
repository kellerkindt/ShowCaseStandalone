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



public interface StorageHandler<H, E> extends Threaded {
    
    
    /**
     * Loads the data and adds it to the given
     * handler
     * @param handler Handler to add the data to
     * @throws IOException
     */
    void loadAll(H handler) throws IOException;
    
    /**
     * Saves all data of the given handler,
     * optional behavior: only save data, that
     * has changed, will also reset it then
     * @param handler Handler to get the data from
     */
    void saveAll(H handler) throws IOException;

    /**
     * Saves the single given data set.
     * @param entity Entity to save
     * @throws IOException
     */
    void save(E entity) throws IOException;
    
    /**
     * All buffered data (if there is one),
     * will now be forced to be written
     * Will block until they are written
     * @throws IOException
     */
    void flush() throws IOException;
}
