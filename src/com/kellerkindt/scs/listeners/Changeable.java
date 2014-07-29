/**
* ShowCaseStandalone
* Copyright (C) 2013 Kellerkindt <copyright at kellerkindt.com>
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
package com.kellerkindt.scs.listeners;

/**
 *
 * @author kellerkindt <michael at kellerkindt.com>
 */
public interface Changeable {

    /**
     * Resets whether this has changed
     */
    public void resetHasChanged ();
    
    /**
     * @return The current hash-code
     */
    public int hashCode ();
    
    /**
     * @return Whether this has changed since the last {@link #hasChanged()}
     */
    public boolean hasChanged ();
}
