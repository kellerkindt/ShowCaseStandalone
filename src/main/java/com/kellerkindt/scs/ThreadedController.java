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

package com.kellerkindt.scs;

import com.kellerkindt.scs.interfaces.Threaded;

import java.io.Flushable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michael <michael at kellerkindt.com>
 */
public class ThreadedController {

    protected List<Threaded> list = new ArrayList<>();
    protected Logger         logger;

    public ThreadedController(Logger logger) {
        this.logger = logger;
    }


    /**
     * @param threaded {@link Threaded} to add
     * @return given {@link Threaded}
     */
    public <T extends Threaded> T add(T threaded) {
        this.list.add(threaded);
        return threaded;
    }

    /**
     * @param threaded {@link Threaded} to remove
     * @return given {@link Threaded}
     */
    public <T extends Threaded> T remove(T threaded) {
        this.list.remove(threaded);
        return threaded;
    }

    /**
     * Starts all known {@link Threaded}
     */
    public void start() {
        for (Threaded threaded : list) {
            threaded.start();
        }
    }

    /**
     * @param join Whether to wait until everyone has stopped
     * @param flush Whether to {@link Flushable#flush()} if possible
     */
    public void stop(boolean join, boolean flush) {
        /*
         * Request any Threaded to stop, without
         * joining, allowing all others to stop
         * while waiting for the last one
         */
        for (Threaded threaded : list) {
            if (flush && threaded instanceof Flushable) {
                try {
                    ((Flushable) threaded).flush();
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, "Failed to flush Threaded="+threaded, t);
                }
            }

            try {
                threaded.stop();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Failed top stop Threaded="+threaded, t);
            }
        }

        if (join) {
            for (Threaded threaded : list) {
                try {
                    threaded.stop(true);
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, "Failed to join Threaded="+threaded, t);
                }
            }
        }
    }
}
