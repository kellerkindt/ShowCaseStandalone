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

/**
 * TODO
 */
public interface Threaded {

    /**
     * @return Whether the worker {@link Thread} is currently running
     */
    boolean isRunning();

    /**
     * Start the internal worker {@link Thread},
     * its totally legal to call this multiple times,
     * although only one worker is possible at once.
     */
    void start();

    /**
     * Stops the worker {@link Thread}, returns immediately
     */
    void stop();

    /**
     * Stops the worker {@link Thread}
     * @param join Whether to wait till the worker {@link Thread} has ended
     * @throws InterruptedException If interrupted while joined, will never happen if join is not requested
     */
    void stop(boolean join) throws InterruptedException;
}
