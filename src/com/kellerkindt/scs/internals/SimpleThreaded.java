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

import com.kellerkindt.scs.interfaces.Threaded;

/**
 * TODO
 */
public abstract class SimpleThreaded implements Threaded {

    protected boolean running;
    protected boolean shallRun;

    protected Thread worker;
    protected String workerName;

    public SimpleThreaded() {
        this(null);
    }

    public SimpleThreaded(String workerName) {
        this.workerName = workerName != null ? workerName : getClass().getSimpleName()+".worker";
        this.running    = false;
        this.shallRun   = false;
    }

    /**
     * Method that gets invoked by the worker {@link Thread}
     */
    protected abstract void run();

    /**
     * @return Whether to keep the worker {@link Thread} running
     */
    protected synchronized boolean keepRunning() {
        return shallRun;
    }

    /**
     * Shall be called if the worker {@link Thread} has been started
     */
    private synchronized void started() {
        this.running = true;
    }

    /**
     * Shall be called if the worker {@link Thread} has been stopped
     */
    private synchronized void stopped() {
        this.running = false;
        this.notifyAll();
    }

    @Override
    public synchronized boolean isRunning() {
        return running;
    }

    @Override
    public synchronized void start() {
        if (!running) {
            this.shallRun = true;
            this.worker   = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        started();
                        SimpleThreaded.this.run();
                    } finally {
                        stopped();
                    }
                }
            }, workerName);

            // actual start
            this.worker.start();
        }
    }

    @Override
    public void stop() {
        try {
            stop(false);
        } catch (InterruptedException ie) {
            // said to not happen
            throw new RuntimeException("This should have never happened :/", ie);
        }
    }

    @Override
    public synchronized void stop(boolean join) throws InterruptedException {
        this.shallRun = false;
        while (running && join) {
            this.wait();
        }
    }
}
