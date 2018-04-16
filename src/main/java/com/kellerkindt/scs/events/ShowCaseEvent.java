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
package com.kellerkindt.scs.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author kellerkindt <michael at kellerkindt.com>
 */
public abstract class ShowCaseEvent extends Event implements Cancellable {

    // Bukkit-EventSystem
    private static final HandlerList    handler    = new HandlerList();

    // cancelled or not
    private boolean     cancelled       = false;
    private boolean     verify          = true;
    private Throwable   cause           = null;
    private String      msgSuccessfully = null;

    private Object consumer;

    /**
     * @param consumer The {@link Object} that wants to select this {@link ShowCaseEvent} as consumed
     * @return Whether this is the first {@link #consume(Object)} call and therefore whether the consumer has been set
     */
    public boolean consume(Object consumer) {
        if (isConsumed()) {
            return false;
        } else {
            this.consumer = consumer;
            return true;
        }
    }

    /**
     * @return Whether this {@link ShowCaseEvent} has been consumed
     */
    public boolean isConsumed() {
        return consumer != null;
    }

    /**
     * @return The consumer of this {@link ShowCaseEvent} or null
     */
    public Object getConsumer() {
        return consumer;
    }


    /**
     * @see org.bukkit.event.Cancellable#isCancelled()
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * @return Whether this Event has a cause why it was cancelled
     */
    public boolean hasCause () {
        return getCause() != null;
    }
    
    /**
     * @return The cause why this event was cancelled or null
     */
    public Throwable getCause () {
        return cause;
    }

    /**
     * @see org.bukkit.event.Cancellable#setCancelled(boolean)
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    /**
     * @param cause Cause to set, why this Event was cancelled
     */
    public void setCause (Throwable cause) {
        this.cause    = cause;
    }
    
    /**
     * Sets whether the event should be verified,
     * if set to false, the event will not be cancelled
     * if i.e. permissions are missing, it will be executed 
     * @param verify Whether the event should be verified
     * @return itself
     */
    public ShowCaseEvent setVerify (boolean verify)  {
        this.verify = verify;
        return this;
    }
    
    /**
     * @return Whether this event should be verified
     */
    public boolean verify () {
        return verify;
    }
    
    /**
     * @param msg Message to display if the event was successfully
     */
    public void setMsgSuccessfully (String msg) {
        this.msgSuccessfully = msg;
    }
    
    /**
     * @return The message to display if the event was successfully
     */
    public String getMsgSuccessfully () {
        return msgSuccessfully;
    }

    /**
     * Bukkit-EventSystem
     */
    public static HandlerList getHandlerList() {
        return handler;
    }

    /**
     * @see org.bukkit.event.Event#getHandlers()
     */
    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    @Override
    public String getEventName() {
        return getClass().getSimpleName();
    }
}
