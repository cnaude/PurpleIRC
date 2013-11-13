/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Events;

import com.cnaude.purpleirc.IRCCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author cnaude
 * Event listener for plugins that want to catch command events from PurpleIRC
 */
public class IRCCommandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();    
    private final IRCCommand ircCommand;

    /**
     *
     * @param ircCommand
     */
    public IRCCommandEvent(IRCCommand ircCommand) {
        this.ircCommand = ircCommand;             
    }
    
    /**
     *
     * @return
     */
    public IRCCommand getIRCCommand() {
        return this.ircCommand;
    }

    /**
     *
     * @return
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     *
     * @return
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}