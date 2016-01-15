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

import java.io.Flushable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO
 */
public abstract class SimpleThreaded<T extends Enum<T>, V> implements Threaded, Flushable {

    protected boolean running;
    protected boolean shallRun;

    protected Thread worker;
    protected String workerName;

    protected Logger logger;

    protected final Queue<Entry> entries = new LinkedList<Entry>();

    public SimpleThreaded(Logger logger) {
        this(logger, null);
    }

    public SimpleThreaded(Logger logger, String workerName) {
        this.logger     = logger;
        this.workerName = workerName != null ? workerName : getClass().getSimpleName()+".worker";
        this.running    = false;
        this.shallRun   = false;
    }

    /**
     * Creates a new queue entry for the given
     * request and value
     *
     * @param request Request to enqueue
     * @param value Value requested
     * @return The given value
     */
    protected V enqueue(T request, V value) {
        synchronized (entries) {
            entries.add(new Entry(request, value));
            entries.notifyAll();
        }
        return value;
    }

    /**
     * Creates new queue entries for each of the
     * given values for the given request
     *
     * @param request Request to enqueue
     * @param values Values requested
     * @return The given values
     */
    protected Iterable<V> enqueue(T request, Iterable<V> values) {
        synchronized (entries) {
            for (V value : values) {
                entries.add(new Entry(request, value));
            }
            entries.notifyAll();
        }
        return values;
    }

    @Override
    public void flush() throws IOException {
        try {
            // there is no reason to wait, if the worker is no longer running
            if (isRunning()) {
                synchronized (entries) {
                    while (entries.size() > 0) {
                        entries.wait();
                    }
                }
            }

        } catch (InterruptedException ie) {
            throw new IOException(ie);
        }
    }

    /**
     * Method that gets invoked by the worker {@link Thread}
     */
    protected void run() {
        Entry entry;

        while (keepRunning()) {
            synchronized (entries) {
                entry = entries.poll();

                if (entry == null) {
                    try {
                        entries.notify();  // notify potential flush
                        entries.wait();    // wait for more work
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Got interrupted, may cause performance issues, "+worker.getName(), ie);
                    }

                    // try again
                    continue;
                }
            }

            // there is work!
            process(entry);
        }
    }

    protected abstract void process(Entry entry);

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
            // wake the worker up
            synchronized (entries) {
                entries.notifyAll();
            }

            // wait for response
            this.wait();
        }
    }

    protected class Entry {
        public final T request;
        public final V value;

        private Entry(T request, V value) {
            this.request = request;
            this.value   = value;
        }
    }
}
