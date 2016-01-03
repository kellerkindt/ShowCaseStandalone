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
